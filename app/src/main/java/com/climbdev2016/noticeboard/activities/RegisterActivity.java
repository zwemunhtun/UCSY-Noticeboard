package com.climbdev2016.noticeboard.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.climbdev2016.noticeboard.R;
import com.climbdev2016.noticeboard.models.User;
import com.climbdev2016.noticeboard.utils.Constants;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import es.dmoral.toasty.Toasty;

import static com.climbdev2016.noticeboard.utils.Constants.CHILD_CODE;
import static com.climbdev2016.noticeboard.utils.Constants.CHILD_USER;
import static com.climbdev2016.noticeboard.utils.Constants.CODE_GALLERY_REQUEST;
import static com.climbdev2016.noticeboard.utils.Constants.FIREBASE_DB_REF;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{


    private ImageView registerUserImage;
    private EditText registerUserName;
    private EditText registerUserOccupation;
    private EditText registerInviteCode;
    private DatabaseReference mCodeRef;
    private String userId;

    private Uri mImageUri = null;
    private ProgressDialog mProgressDialog;

    private DatabaseReference mUserRef;
    private FirebaseUser mUser;
    private StorageReference mProfileRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mUserRef = FIREBASE_DB_REF.child(CHILD_USER);
        mCodeRef = FIREBASE_DB_REF.child(CHILD_CODE);
        mProfileRef = FirebaseStorage.getInstance().getReference().child(getString(R.string.child_profile_images));
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mUser != null) {
            userId = mUser.getUid();
        }

        mProgressDialog = new ProgressDialog(this);

        registerUserImage = (ImageView) findViewById(R.id.register_user_image);
        registerUserName = (EditText) findViewById(R.id.register_user_name);
        registerUserOccupation = (EditText) findViewById(R.id.register_user_occupation);
        registerInviteCode = (EditText) findViewById(R.id.register_invite_code);
        Button btnRegisterUser = (Button) findViewById(R.id.btnRegisterUser);

        registerUserImage.setOnClickListener(this);
        btnRegisterUser.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnRegisterUser:
                startSetupAccount();
                break;
            case R.id.register_user_image:
                callCameraAction();
                break;
        }
    }

    private void callCameraAction() {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, CODE_GALLERY_REQUEST);
    }

    private void startSetupAccount() {

        final String name = registerUserName.getText().toString().trim();
        final String occupation = registerUserOccupation.getText().toString().trim();
        final String inviteCode = registerInviteCode.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            registerUserName.setError("Username can't be blank.");
        } else if (TextUtils.isEmpty(occupation)) {
            registerUserOccupation.setError("Occupation can't be blank");
        } else if (mImageUri == null){
            Toasty.warning(RegisterActivity.this,"You must set your profile.",Toast.LENGTH_SHORT).show();
        } else if (inviteCode == null){
            Toasty.warning(RegisterActivity.this,"Need Invite Code",Toast.LENGTH_SHORT).show();
        }
        else {
            mProgressDialog.setMessage(getString(R.string.setup_acc_txt));
            mProgressDialog.show();

            mCodeRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild(inviteCode)){
                        StorageReference filepath = mProfileRef.child(mImageUri.getLastPathSegment());
                        filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                String downloadUrl = null;
                                if (taskSnapshot.getDownloadUrl() != null) {
                                    downloadUrl = taskSnapshot.getDownloadUrl().toString();
                                }

                                User user = new User(downloadUrl, name, occupation);
                                mUserRef.child(userId).setValue(user);

                                mProgressDialog.dismiss();
                                goToMain();
                            }
                        });
                    }else {
                        Toasty.error(RegisterActivity.this, "Invite Code is worng!", Toast.LENGTH_SHORT, true).show();
                        mProgressDialog.dismiss();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

    }

    private void goToMain() {
        Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CODE_GALLERY_REQUEST && resultCode == RESULT_OK){
            CropImage.activity(data.getData()).setCropShape(CropImageView.CropShape.OVAL)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1).start(this);
        }
        else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                mImageUri = result.getUri();
                registerUserImage.setImageURI(mImageUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                mImageUri = null;
                Toasty.error(this, "Crop failed.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}


