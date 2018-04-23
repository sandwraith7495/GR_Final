package com.example.sandwraith8.gr_final;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sandwraith8.gr_final.common.StringUtil;
import com.example.sandwraith8.gr_final.model.Contact;
import com.example.sandwraith8.gr_final.repository.local.ContactRepository;
import com.example.sandwraith8.gr_final.repository.local.HumanNameRepository;
import com.example.sandwraith8.gr_final.service.BitmapService;
import com.example.sandwraith8.gr_final.service.ContactExtractor;
import com.example.sandwraith8.gr_final.service.GoogleService;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.Text;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_GALLERY = 0;
    private static final int REQUEST_CAMERA = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private ImageView imageView;
    private static final String TAG = MainActivity.class.getSimpleName();

    private String imagePath;
    private TextView detectedTextView;
    private TextView displayEmail;
    private TextView displayPhone;
    private TextView displayName;
    private TextView openContacts;
    private Button signOutButton;
    private GoogleSignInClient mGoogleSignInClient;
    private Button list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initLayout();
        btnListenerInit();
        if (savedInstanceState != null) {
            imagePath = savedInstanceState.getString("imagePath");
        }

        mGoogleSignInClient = GoogleService.getInstance().getClient(this);
        list = findViewById(R.id.list_contact);
        list.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent list = new Intent(MainActivity.this, ListContact.class);
                startActivity(list);
            }
        });
//        Intent intent = getIntent();
//        String id = intent.getStringExtra("account");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("imagePath", imagePath);
        super.onSaveInstanceState(outState);
    }

    private void initLayout() {
        signOutButton = findViewById(R.id.sign_out_button);
        imageView = findViewById(R.id.img);
        displayName = findViewById(R.id.displayName);
        displayPhone = findViewById(R.id.displayPhone);
        displayEmail = findViewById(R.id.displayEmail);
//        detectedTextView = findViewById(R.id.detected_text);
//        detectedTextView.setMovementMethod(new ScrollingMovementMethod());
        openContacts = findViewById(R.id.openContacts);
    }

    private void btnListenerInit() {
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGoogleSignInClient.signOut()
                        .addOnCompleteListener(MainActivity.this, new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                finish();
                            }
                        });
            }
        });

        openContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToContacts();
            }
        });

        findViewById(R.id.choose_from_gallery).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, REQUEST_GALLERY);
            }
        });

        findViewById(R.id.take_a_photo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    if (photoFile != null) {
                        imagePath = photoFile.getAbsolutePath();
                        Uri photoURI = FileProvider.getUriForFile(MainActivity.this,
                                "com.example.android.fileprovider",
                                photoFile);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(takePictureIntent, REQUEST_CAMERA);
                    }
                }
            }
        });
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(imagePath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        imagePath = image.getAbsolutePath();
        return image;
    }

    private void inspectFromBitmap(Bitmap bitmap) {
        TextRecognizer textRecognizer = new TextRecognizer.Builder(this).build();
        String[] extractData = null;
        try {
            if (!textRecognizer.isOperational()) {
                new AlertDialog.
                        Builder(this).
                        setMessage("Text recognizer could not be set up on your device").show();
                return;
            }

            Frame frame = new Frame.Builder().setBitmap(bitmap).build();
            SparseArray<TextBlock> origTextBlocks = textRecognizer.detect(frame);
            List<TextBlock> textBlocks = new ArrayList<>();
            ArrayList<Point[]> linePoints = new ArrayList<>();
            for (int i = 0; i < origTextBlocks.size(); i++) {
                Point[] points = new Point[origTextBlocks.size()];
                TextBlock textBlock = origTextBlocks.valueAt(i);
                textBlocks.add(textBlock);
                for(Text line: textBlock.getComponents()){
                    points = line.getCornerPoints();
                    linePoints.add(points);
                }
            }
            Collections.sort(textBlocks, new Comparator<TextBlock>() {
                @Override
                public int compare(TextBlock o1, TextBlock o2) {
                    int diffOfTops = o1.getBoundingBox().top - o2.getBoundingBox().top;
                    int diffOfLefts = o1.getBoundingBox().left - o2.getBoundingBox().left;
                    if (diffOfTops != 0) {
                        return diffOfTops;
                    }
                    return diffOfLefts;
                }
            });

            StringBuilder detectedText = new StringBuilder();
            for (TextBlock textBlock : textBlocks) {
                if (textBlock != null && textBlock.getValue() != null) {
                    detectedText.append(textBlock.getValue());
                    detectedText.append("\n");
                }
            }

//            detectedTextView.setText(Arrays.toString(linePoints));
            extractData = detectedText.toString().split("\n");

            String name = ContactExtractor.getInstance().extractName(extractData);
            if (!StringUtil.isEmpty(name)) {
                displayName.setText(name);
            }
            String email = ContactExtractor.getInstance().extractEmail(extractData);
            if (!StringUtil.isEmpty(email)) {
                displayEmail.setText(email);
            }
            List<String> phones = ContactExtractor.getInstance().extractPhone(extractData);
            if (phones.size() > 0) {
                displayPhone.setText(phones.get(0));
            }

        Contact contact = new Contact();
        contact.setEmail(email);
        contact.setName(name);
        contact.setPhones(phones);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();
        String image = Base64.encodeToString(byteArray, Base64.DEFAULT);
        contact.setImage(image);
        ContactRepository.getInstance().add(contact);
        Toast.makeText(this, "Your contact have been save", Toast.LENGTH_LONG).show();
//        contact = ContactRepository.getInstance().find(email);
        } finally {
            textRecognizer.release();
        }
    }

    private void inspect(Uri uri) {
        Bitmap bitmap = BitmapService.getInstance().getBitmap(this, uri);
        if (bitmap != null) {
            inspectFromBitmap(bitmap);
        } else {
            Toast.makeText(this, "Loi", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_GALLERY:
                if (resultCode == RESULT_OK) {
                    inspect(data.getData());
                    Uri pickedImage = data.getData();
                    String[] filePath = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(pickedImage, filePath, null, null, null);
                    cursor.moveToFirst();
                    String imagePath = cursor.getString(cursor.getColumnIndex(filePath[0]));
                    Bitmap imageBitmap1 = BitmapService.getInstance().decodeSampledBitmapFromFile(imagePath);
                    imageView.setImageBitmap(imageBitmap1);
                }
                break;
            case REQUEST_CAMERA:
                if (resultCode == RESULT_OK) {
                    Bitmap image = BitmapService.getInstance().decodeSampledBitmapFromFile(imagePath);
                    if (image != null) {
                        galleryAddPic();
                        inspectFromBitmap(image);
                        imageView.setImageBitmap(image);
                    } else {
                        Toast.makeText(this, "Loi", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    private void addToContacts() {
        Intent intent = new Intent(ContactsContract.Intents.Insert.ACTION);
        intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
        if (displayName.getText().length() > 0 || (displayPhone.getText().length() > 0 || displayEmail.getText().length() > 0)) {
            intent.putExtra(ContactsContract.Intents.Insert.NAME, displayName.getText());
            intent.putExtra(ContactsContract.Intents.Insert.EMAIL, displayEmail.getText());
            intent.putExtra(ContactsContract.Intents.Insert.EMAIL_TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK);
            intent.putExtra(ContactsContract.Intents.Insert.PHONE, displayPhone.getText());
            intent.putExtra(ContactsContract.Intents.Insert.PHONE_TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_WORK);
            startActivity(intent);
        } else {
            Toast.makeText(getApplicationContext(), "No information to add to contacts!", Toast.LENGTH_LONG).show();
        }
    }
}
