/*******************************************************************************
 * Copyright (C) 2014 Artem Yankovskiy (artemyankovskiy@gmail.com).
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package ru.neverdark.phototools.fragments;

import ru.neverdark.phototools.R;
import ru.neverdark.phototools.db.DbAdapter;
import ru.neverdark.phototools.db.UserCamerasRecord;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;

import com.actionbarsherlock.app.SherlockDialogFragment;

public class CameraEditorDialog extends SherlockDialogFragment {
    private class CheckedChangeListener implements OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton buttonView,
                boolean isChecked) {
            disableCustomCoc(isChecked);
        }
    }

    private class CameraEditorDialogException extends Exception {
        private static final long serialVersionUID = -3433785378864484422L;

        public CameraEditorDialogException(int resourceId) {
            ShowMessageDialog dialog = new ShowMessageDialog();
            dialog.setMessages(R.string.error, resourceId);
            dialog.show(getFragmentManager(), ShowMessageDialog.DIALOG_TAG);
        }
    }

    private class NegativeClickListener implements OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            dialog.cancel();
        }
    }

    public interface OnCameraEditorListener {
        public void onAddCamera(UserCamerasRecord record);

        public void onEditCamera(UserCamerasRecord record);
    }

    private class PositiveClickListener implements OnClickListener {
        private boolean isDataValid() {

            float resolutionWidth = Float.valueOf(mResolutionWidth.getText()
                    .toString());
            float resolutionHeight = Float.valueOf(mResolutionHeight.getText()
                    .toString());
            float sensorWidth = Float
                    .valueOf(mSensorWidth.getText().toString());
            float sensorHeight = Float.valueOf(mSensorHeight.getText()
                    .toString());

            return (sensorWidth / resolutionWidth) == (sensorHeight / resolutionHeight);
        }

        private boolean isDataFilled() {
            boolean isFilled;

            isFilled = mResolutionWidth.getText().toString().trim().length() > 0;

            if (isFilled) {
                isFilled = mResolutionHeight.getText().toString().trim()
                        .length() > 0;
            }

            if (isFilled) {
                isFilled = mSensorWidth.getText().toString().trim().length() > 0;
            }

            if (isFilled) {
                isFilled = mSensorHeight.getText().toString().trim().length() > 0;
            }

            return isFilled;
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            String cameraName = mCameraModel.getText().toString().trim();

            try {
                if (cameraName.length() == 0) {
                    throw new CameraEditorDialogException(
                            R.string.error_cameraNameEmpty);
                }

                if (isDataFilled() == false) {
                    throw new CameraEditorDialogException(
                            R.string.error_notAllValuesFilled);
                }

                if (isDataValid() == false) {
                    throw new CameraEditorDialogException(
                            R.string.error_incorrectData);
                }

                if (mAutoCalcCoc.isChecked() == false
                        && mCoc.getText().toString().trim().length() == 0) {
                    throw new CameraEditorDialogException(
                            R.string.error_cocNotFilled);
                }
                
                boolean exist = false;

                if ((mActionType == ACTION_ADD)
                        || (mActionType == ACTION_EDIT && cameraName != mUserCamerasRecord
                                .getCameraName())) {
                    DbAdapter dbAdapter = new DbAdapter(mContext).open();
                    exist = dbAdapter.getUserCameras()
                            .isCameraExist(cameraName);
                    dbAdapter.close();
                }

                if (exist) {
                    throw new CameraEditorDialogException(
                            R.string.error_cameraAlreadyExist);
                }

                dialog.dismiss();

                if (mCallback != null) {
                    fillUserCamerasRecord(mUserCamerasRecord);

                    if (mActionType == ACTION_ADD) {
                        mCallback.onAddCamera(mUserCamerasRecord);
                    } else if (mActionType == ACTION_EDIT) {
                        mCallback.onEditCamera(mUserCamerasRecord);
                    }
                }
            } catch (CameraEditorDialogException e) {

            }
        }
    }

    public static final String DIALOG_TAG = "cameraEditorDialog";
    public static final int ACTION_ADD = 0;
    public static final int ACTION_EDIT = 1;

    public static CameraEditorDialog getInstance(Context context) {
        CameraEditorDialog dialog = new CameraEditorDialog();
        dialog.mContext = context;
        return dialog;
    }

    private OnCameraEditorListener mCallback;
    private Context mContext;
    private View mView;
    private AlertDialog.Builder mAlertDialog;
    private String mCameraName;
    private UserCamerasRecord mUserCamerasRecord;
    private EditText mResolutionWidth;
    private EditText mResolutionHeight;
    private EditText mSensorWidth;
    private EditText mSensorHeight;
    private CheckBox mAutoCalcCoc;
    private EditText mCoc;
    private EditText mCameraModel;

    private int mActionType;

    private void bindObjectToResource() {
        mView = View.inflate(mContext, R.layout.camera_editor_dialog, null);
        mResolutionWidth = (EditText) mView
                .findViewById(R.id.cameraEditor_resolutionWidth);
        mResolutionHeight = (EditText) mView
                .findViewById(R.id.cameraEditor_resolutionHeight);
        mSensorWidth = (EditText) mView
                .findViewById(R.id.cameraEditor_sensorWidth);
        mSensorHeight = (EditText) mView
                .findViewById(R.id.cameraEditor_sensorHeight);
        mCameraModel = (EditText) mView
                .findViewById(R.id.cameraEditor_cameraName);
        mCoc = (EditText) mView.findViewById(R.id.cameraEditor_coc);
        mAutoCalcCoc = (CheckBox) mView
                .findViewById(R.id.cameraEditor_autoCalc);
    }

    private void disableCustomCoc(boolean isDisable) {
        mCoc.setEnabled(!isDisable);
    }

    private void createDialog() {
        mAlertDialog = new AlertDialog.Builder(mContext);
        mAlertDialog.setView(mView);

        if (mActionType == ACTION_ADD) {
            mAlertDialog.setTitle(R.string.userCamera_addCamera);
            disableCustomCoc(true);
            mCameraModel.setText(mCameraName);
        } else if (mActionType == ACTION_EDIT) {
            mAlertDialog.setTitle(R.string.userCamera_editCamera);

            boolean disableCoc = !mUserCamerasRecord.isCustomCoc();
            String cameraName = mUserCamerasRecord.getCameraName();
            String resolutionWidth = String.valueOf(mUserCamerasRecord
                    .getResolutionWidth());
            String resolutionHeight = String.valueOf(mUserCamerasRecord
                    .getResolutionHeight());
            String sensorWidth = String.valueOf(mUserCamerasRecord
                    .getSensorWidth());
            String sensorHeight = String.valueOf(mUserCamerasRecord
                    .getSensorHeight());
            String coc = String.valueOf(mUserCamerasRecord.getCoc());

            disableCustomCoc(disableCoc);
            mCameraModel.setText(cameraName);
            mResolutionWidth.setText(resolutionWidth);
            mResolutionHeight.setText(resolutionHeight);
            mSensorWidth.setText(sensorWidth);
            mSensorHeight.setText(sensorHeight);
            mCoc.setText(coc);
            mAutoCalcCoc.setChecked(disableCoc);
        }
    }

    private void fillUserCamerasRecord(UserCamerasRecord record) {
        if (mActionType == ACTION_ADD) {
            record = new UserCamerasRecord();
        }

        String cameraName = mCameraModel.getText().toString().trim();
        boolean isCustomCoc = !mAutoCalcCoc.isChecked();
        int resolutionWidth = Integer.valueOf(mResolutionWidth.getText().toString().trim());
        int resolutionHeight = Integer.valueOf(mResolutionHeight.getText().toString().trim());
        float sensorWidth = Float.valueOf(mSensorWidth.getText().toString().trim());
        float sensorHeight = Float.valueOf(mSensorHeight.getText().toString().trim());
        float coc = Float.valueOf(mCoc.getText().toString().trim());
        
        record.setCameraName(cameraName);
        record.setCoc(coc);
        record.setIsCustomCoc(isCustomCoc);
        record.setResolutionHeight(resolutionHeight);
        record.setResolutionWidth(resolutionWidth);
        record.setSensorHeight(sensorHeight);
        record.setSensorWidth(sensorWidth);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        bindObjectToResource();
        createDialog();
        setOnClickListener();

        return mAlertDialog.create();
    }

    public void setActionType(int actionType) {
        mActionType = actionType;
    }

    public void setCallback(OnCameraEditorListener callback) {
        mCallback = callback;
    }

    public void setCameraName(String cameraName) {
        mCameraName = cameraName;
    }

    public void setDataForEdit(UserCamerasRecord record) {
        mUserCamerasRecord = record;
    }

    private void setOnClickListener() {
        mAlertDialog.setPositiveButton(R.string.dialog_button_ok,
                new PositiveClickListener());
        mAlertDialog.setNegativeButton(R.string.dialog_button_cancel,
                new NegativeClickListener());
        mAutoCalcCoc.setOnCheckedChangeListener(new CheckedChangeListener());
    }
}
