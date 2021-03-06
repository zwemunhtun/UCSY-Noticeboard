package com.climbdev2016.noticeboard.utils;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Constants {

    public static final int CODE_GALLERY_REQUEST = 3;
    public static final int CODE_PROFILE_GALLERY_REQUEST = 1;

    public static final String SHARE_HASH_TAG = "#Noticeboard";

    public static final int MAIN_VIEW = 1;
    public static final int CATEGORY_VIEW = 2;
    public static final int PROFILE_VIEW = 3;
    public static final int PENDING_VIEW = 4;
    public static final int DELETE_POST_VIEW = 5;


    public static final DatabaseReference FIREBASE_DB_REF =
            FirebaseDatabase.getInstance().getReference();

    public static final String CHILD_POST = "Post";
    public static final String CHILD_USER = "Users";
    public static final String CHILD_LINK = "Link";
    public static final String CHILD_CODE = "Code";
    public static final String CHILD_ADMIN = "Admin";

    public static final String SUB_CHILD_LINK = "link";
    public static final String SUB_CHILD_CATEGORY = "category";
    public static final String SUB_CHILD_OWNER_ID = "owner_id";
    public static final String SUB_CHILD_POST_APPROVE = "postApprove";

    public static final String APPROVE_YES = "Yes";
    public static final String APPROVE_NO = "No";

}
