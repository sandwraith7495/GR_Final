package com.example.sandwraith8.gr_final.fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Base64;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sandwraith8.gr_final.R;
import com.example.sandwraith8.gr_final.common.StringUtil;
import com.example.sandwraith8.gr_final.model.Contact;
import com.example.sandwraith8.gr_final.repository.local.ContactRepository;
import com.example.sandwraith8.gr_final.service.BitmapService;
import com.example.sandwraith8.gr_final.service.ContactExtractor;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.Line;
import com.google.android.gms.vision.text.Text;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;
import static com.example.sandwraith8.gr_final.common.StringUtil.removeAccent;

/**
 * Created by sandwraith8 on 21/04/2018.
 */

public class MainFragment extends BaseFragment {
    private static final int REQUEST_GALLERY = 0;
    private static final int REQUEST_CAMERA = 1;
    private ImageView imageView;
    private Button save;

    private String imagePath;
    private TextView displayEmail;
    private TextView displayPhone;
    private TextView displayName;
    private TextView openContacts;
    private Button galeryButton, cameraButton;

    public MainFragment() {
        super(R.layout.activity_main);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("imagePath", imagePath);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void restoreView(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            imagePath = savedInstanceState.getString("imagePath");
        }
    }

    @Override
    public void init(View v) {
        initLayout(v);
        btnListenerInit();
    }

    private void initLayout(View v) {
        imageView = v.findViewById(R.id.img);
        displayName = v.findViewById(R.id.displayName);
        displayPhone = v.findViewById(R.id.displayPhone);
        displayEmail = v.findViewById(R.id.displayEmail);
        openContacts = v.findViewById(R.id.openContacts);
        galeryButton = v.findViewById(R.id.choose_from_gallery);
        cameraButton = v.findViewById(R.id.take_a_photo);
        save = v.findViewById(R.id.save);
    }

    private void btnListenerInit() {
        openContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToContacts();
            }
        });

        galeryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, REQUEST_GALLERY);
            }
        });

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getContext().getPackageManager()) != null) {
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    if (photoFile != null) {
                        imagePath = photoFile.getAbsolutePath();
                        Uri photoURI = FileProvider.getUriForFile(getContext(),
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
        getActivity().sendBroadcast(mediaScanIntent);
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        imagePath = image.getAbsolutePath();
        return image;
    }

    private void inspectFromBitmap(Bitmap bitmap) {
        TextRecognizer textRecognizer = new TextRecognizer.Builder(getContext()).build();
        String[] extractData;
        ArrayList<String> convertedData = new ArrayList<String>();
        try {
            if (!textRecognizer.isOperational()) {
                new AlertDialog.
                        Builder(getContext()).
                        setMessage("Text recognizer could not be set up on your device").show();
                return;
            }

            Frame frame = new Frame.Builder().setBitmap(bitmap).build();
            SparseArray<TextBlock> origTextBlocks = textRecognizer.detect(frame);
            List<TextBlock> textBlocks = new ArrayList<>();
//            List<Line> lines = new ArrayList<>();
            double maxDistance = 0.0;
            String lines = "";
            String name = "";
            for (int i = 0; i < origTextBlocks.size(); i++) {
                Point[] points = new Point[origTextBlocks.size()];
                TextBlock textBlock = origTextBlocks.valueAt(i);
                for (Text line : textBlock.getComponents()){
                    points = line.getCornerPoints();
                    Point point1 = points[0];
                    Point point4 = points[3];
                    double distance = Math.sqrt((point1.x - point4.x) * (point1.x - point4.x) + (point1.y - point4.y) * (point1.y - point4.y));
                    if (distance > maxDistance){
                        name = line.getValue();
                        maxDistance = distance;
                    }
                    lines = lines + line.getValue() + "\n";
                }
                textBlocks.add(textBlock);
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

//            extractData = detectedText.toString().split("\n");
            extractData = lines.split("\n");
            for (String data : extractData){
                data = removeAccent(data);
                convertedData.add(data);
            }

            if (!StringUtil.isEmpty(name)) {
                displayName.setText(name);
            }
            String email = ContactExtractor.getInstance().extractEmail(convertedData);
            if (!StringUtil.isEmpty(email)) {
                displayEmail.setText(email);
            }
            List<String> phones = ContactExtractor.getInstance().extractPhone(convertedData);
            if (phones.size() > 0) {
                displayPhone.setText(phones.get(0));
            }

            final Contact contact = new Contact();
            contact.setEmail(email);
            contact.setName(name);
            contact.setPhones(phones);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap = resizeBitmap(bitmap, bitmap.getHeight() / 3, bitmap.getWidth() / 3);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            String image = Base64.encodeToString(byteArray, Base64.DEFAULT);
            contact.setImage(image);
            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ContactRepository.getInstance().add(contact);
                    Toast.makeText(getContext(), "Your contact have been save", Toast.LENGTH_LONG).show();
                }
            });
//        contact = ContactRepository.getInstance().find(email);
        } finally {
            textRecognizer.release();
        }
    }

    public static Bitmap resizeBitmap(Bitmap bitmap, int newHeight, int newWidth) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false);
        return resizedBitmap;
    }

    private void inspect(Uri uri) {
        Bitmap bitmap = BitmapService.getInstance().getBitmap(getActivity(), uri);
        if (bitmap != null) {
            inspectFromBitmap(bitmap);
        } else {
            Toast.makeText(getContext(), "Loi", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(getContext(), "No information to add to contacts!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_GALLERY:
                if (resultCode == RESULT_OK) {
                    inspect(data.getData());
                    Uri pickedImage = data.getData();
                    String[] filePath = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContext().getContentResolver().query(pickedImage, filePath, null, null, null);
                    if (cursor.moveToFirst()) {
                        String imagePath = cursor.getString(cursor.getColumnIndex(filePath[0]));
                        Bitmap imageBitmap1 = BitmapService.getInstance().decodeSampledBitmapFromFile(imagePath);
                        imageView.setImageBitmap(imageBitmap1);
                    } else {
                        Toast.makeText(getContext(), "Loi", Toast.LENGTH_SHORT).show();
                    }
                    cursor.close();
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
                        Toast.makeText(getContext(), "Loi", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }
}
