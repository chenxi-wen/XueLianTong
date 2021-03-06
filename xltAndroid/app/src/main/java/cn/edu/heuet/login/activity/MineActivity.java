package cn.edu.heuet.login.activity;

import static com.vondear.rxui.view.dialog.RxDialogChooseImage.LayoutType.TITLE;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.vondear.rxtool.RxBarTool;
import com.vondear.rxtool.RxDeviceTool;
import com.vondear.rxtool.RxPhotoTool;
import com.vondear.rxtool.RxSPTool;
import com.vondear.rxtool.RxTool;
import com.vondear.rxui.activity.ActivityBase;
import com.vondear.rxui.view.RxTitle;
import com.vondear.rxui.view.dialog.RxDialogChooseImage;
import com.vondear.rxui.view.dialog.RxDialogScaleView;
import com.vondear.rxui.view.dialog.RxDialogSureCancel;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropActivity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import cn.edu.heuet.login.R;


/**
 * @author vondear
 */
public class MineActivity extends ActivityBase implements View.OnClickListener {

    private Uri resultUri;
    private RxTitle mRxTitle;
    private TextView mTvBg;
    private ImageView mIvAvatar;
    private LinearLayout mLlAnchorLeft;
    private RelativeLayout mRlAvatar;
    private TextView mTvName;
    private TextView mTvConstellation;
    private TextView mTvBirthday;
    private TextView mTvAddress;
    private TextView mTvLables;
    private TextView mTextView2;
    private TextView mEditText2;
    private Button mBtnExit;
    private LinearLayout mActivityUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RxBarTool.noTitle(this);

        RxTool.init(this);

        setContentView(R.layout.activity_mine);
        initUI();
        RxDeviceTool.setPortrait(this);
        initView();
    }

    private void initUI(){
        mRxTitle = findViewById(R.id.rx_title);
        mTvBg = findViewById(R.id.tv_bg);
        mIvAvatar = findViewById(R.id.iv_avatar);
        mLlAnchorLeft = findViewById(R.id.ll_anchor_left);
        mRlAvatar = findViewById(R.id.rl_avatar);
        mTvName = findViewById(R.id.tv_name);
        mTvConstellation = findViewById(R.id.tv_constellation);
        mTvBirthday = findViewById(R.id.tv_birthday);
        mTvAddress = findViewById(R.id.tv_address);
        //mTvLables = findViewById(R.id.tv_lables);
        mTextView2 = findViewById(R.id.textView2);
        mEditText2 = findViewById(R.id.editText2);
        mBtnExit = findViewById(R.id.btn_exit);
        mActivityUser = findViewById(R.id.activity_user);
    }

    protected void initView() {
        Resources r = mContext.getResources();
        resultUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"
                + r.getResourcePackageName(R.drawable.circle_elves_ball) + "/"
                + r.getResourceTypeName(R.drawable.circle_elves_ball) + "/"
                + r.getResourceEntryName(R.drawable.circle_elves_ball));

        mRxTitle.setLeftFinish(mContext);

        mIvAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initDialogChooseImage();
            }
        });
        mIvAvatar.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
//                RxImageTool.showBigImageView(mContext, resultUri);
                RxDialogScaleView rxDialogScaleView = new RxDialogScaleView(mContext);
                rxDialogScaleView.setImage(resultUri);
                rxDialogScaleView.show();
                return false;
            }
        });
    }

    private void initDialogChooseImage() {
        RxDialogChooseImage dialogChooseImage = new RxDialogChooseImage(mContext, TITLE);
        dialogChooseImage.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RxPhotoTool.GET_IMAGE_FROM_PHONE://???????????????????????????
                if (resultCode == RESULT_OK) {
//                    RxPhotoTool.cropImage(ActivityUser.this, );// ????????????
                    initUCrop(data.getData());
                }

                break;
            case RxPhotoTool.GET_IMAGE_BY_CAMERA://??????????????????????????????
                if (resultCode == RESULT_OK) {
                    /* data.getExtras().get("data");*/
//                    RxPhotoTool.cropImage(ActivityUser.this, RxPhotoTool.imageUriFromCamera);// ????????????
                    initUCrop(RxPhotoTool.imageUriFromCamera);
                }

                break;
            case RxPhotoTool.CROP_IMAGE://????????????????????????
                RequestOptions options = new RequestOptions()
                        .placeholder(R.drawable.circle_elves_ball)
                        //???????????????(???????????????????????????????????????)
                        .error(R.drawable.circle_elves_ball)
                        //??????Glide??????????????????
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE);

                Glide.with(mContext).
                        load(RxPhotoTool.cropImageUri).
                        apply(options).
                        thumbnail(0.5f).
                        into(mIvAvatar);
//                RequestUpdateAvatar(new File(RxPhotoTool.getRealFilePath(mContext, RxPhotoTool.cropImageUri)));
                break;

            case UCrop.REQUEST_CROP://UCrop?????????????????????
                if (resultCode == RESULT_OK) {
                    resultUri = UCrop.getOutput(data);
                    roadImageView(resultUri, mIvAvatar);
                    RxSPTool.putContent(mContext, "AVATAR", resultUri.toString());
                } else if (resultCode == UCrop.RESULT_ERROR) {
                    final Throwable cropError = UCrop.getError(data);
                }
                break;
            case UCrop.RESULT_ERROR://UCrop???????????????????????????
                final Throwable cropError = UCrop.getError(data);
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //???Uri??????????????? ??????????????????File????????????
    private File roadImageView(Uri uri, ImageView imageView) {
        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.circle_elves_ball)
                //???????????????(???????????????????????????????????????)
                .error(R.drawable.circle_elves_ball)
                .transform(new CircleCrop())
                //??????Glide??????????????????
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE);

        Glide.with(mContext).
                load(uri).
                apply(options).
                thumbnail(0.5f).
                into(imageView);

        return (new File(RxPhotoTool.getImageAbsolutePath(this, uri)));
    }

    private void initUCrop(Uri uri) {
        SimpleDateFormat timeFormatter = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA);
        long time = System.currentTimeMillis();
        String imageName = timeFormatter.format(new Date(time));

        Uri destinationUri = Uri.fromFile(new File(getCacheDir(), imageName + ".jpeg"));

        UCrop.Options options = new UCrop.Options();
        //????????????????????????????????????
        options.setAllowedGestures(UCropActivity.SCALE, UCropActivity.ROTATE, UCropActivity.ALL);
        //???????????????????????????????????????
        //options.setHideBottomControls(true);
        //??????toolbar??????
        options.setToolbarColor(ActivityCompat.getColor(this, R.color.colorPrimary));
        //?????????????????????
        options.setStatusBarColor(ActivityCompat.getColor(this, R.color.colorPrimaryDark));

        //????????????
        //????????????????????????
        options.setMaxScaleMultiplier(5);
        //???????????????????????????????????????
        options.setImageToCropBoundsAnimDuration(666);
        //?????????????????????????????????
        //options.setCircleDimmedLayer(true);
        //?????????????????????????????????
        // options.setShowCropFrame(false);
        //?????????????????????????????????
        //options.setCropGridStrokeWidth(20);
        //?????????????????????????????????
        //options.setCropGridColor(Color.GREEN);
        //?????????????????????
        //options.setCropGridColumnCount(2);
        //?????????????????????
        //options.setCropGridRowCount(1);

        UCrop.of(uri, destinationUri)
                .withAspectRatio(1, 1)
                .withMaxResultSize(1000, 1000)
                .withOptions(options)
                .start(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_exit:
                final RxDialogSureCancel rxDialogSureCancel = new RxDialogSureCancel(this);
                rxDialogSureCancel.getCancelView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        rxDialogSureCancel.cancel();
                    }
                });
                rxDialogSureCancel.getSureView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
                rxDialogSureCancel.show();
        }
    }
}
