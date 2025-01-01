<img src="../../img/icon.png" height="150" align="right"/>

# PasswordVaultBottomSheetDialog.Callback
```java
public static interface PasswordVaultBottomSheetDialog.Callback implements Serializable
```
Callback for all [bottom sheet dialogs](PasswordVaultBottomSheetDialog.md) informs the host of the dialog about successful or cancelled dialog actions.

###### Table of Contents
1. [Overview](#overview)
2. [Public Fields](#public-fields)
3. [Public Methods](#public-methods)

<br/>

## Overview
This callback provides basic functionality to inform the host of a dialog about the dialog finishing (or closing) successfully or cancelling.

Invoke a callback in your dialog as follows:
```java
public class MyDialog extends PasswordVaultBottomSheetDialog<MyViewModel> {

    public MyDialog() {
        super(MyViewModel.class, R.layout.dialog_my);
    }


    @Override
    @NonNull
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        if (view == null) {
            return null;
        }

        //Invoke dialog callbacks:
        view.findViewById(R.id.button_ok).setOnClickListener(v -> {
            if (callback != null) {
                //Success:
                callback.onCallback(this, Callback.RESULT_SUCCESS);
            }
            dismiss();
        });
        view.findViewById(R.id.button_cancel).setOnClickListener(v -> {
            if (callback != null) {
                //Cancel:
                callback.onCallback(this, Callback.RESULT_CANCEL);
            }
            dismiss();
        });

        return view;
    }

}
```

In order to receive a callback within the host of a dialog, the host should implement the `Callback` interface:
```java
public class MyActivity extends PasswordVaultActivity<MyActivityViewModel> implements PasswordVaultBottomSheetDialog.Callback {

    public MyActivity() {
        super(MyActivityViewModel.class, R.layout.activity_my);
    }


    @Override
    public void onCallback(PasswordVaultBottomSheetDialog dialog, int resultCode) {
        if (resultCode == PasswordVaultBottomSheetDialog.Callback.RESULT_SUCCESS) {
            //Dialog ended successfully...
        }
        else if (resultCode == PasswordVaultBottomSheetDialog.Callback.RESULT_CANCEL) {
            //Dialog ended unsuccessfully
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Create a dialog with a callback to this activity:
        MyDialog dialog = new MyDialog();
        dialog.show(getSupportFragmentManager(), null);
    }

}
```

<br/>

Alternatively, an instance of a callback can be passed as argument to the constructor when creating the dialog if the dialog is started from a class that is no activity. For this, the dialog needs a separate constructor:
```java
public class MySecondDialog extends PasswordVaultBottomSheetDialog<MySecondViewModel> {

    public MySecondDialog(@Nullable Callback attachableCallback) {
        super(MySecondViewModel.class, R.layout.dialog_my, attachableCallback);
    }


    @Override
    @NonNull
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //...
    }

}
```

Create the dialog as follows:
```java
public class MyClassThatIsNoActivity {
    
    private static class MyCallback implements PasswordVaultBottomSheetDialog.Callback {

        @Override
        public void onCallback(PasswordVaultBottomSheetDialog dialog, int resultCode) {
            //Handle callback
        }

    }

    public void instantiateDialog(@NonNull Context context) {
        MyCallback callback = new MyCallback();

        MySecondDialog dialog = new MySecondCallback(callback);
        dialog.show(context.getSupportFragmentManager(), null);
    }

}
```


<br/>

## Summary

###### Public Fields
Field | Description
--- | ---
[`RESULT_SUCCESS`](#result_success) | Result code to pass when a dialog closes successfully.
[`RESULT_CANCEL`](#result_cancel) | Result code to pass when a dialog closes unsuccessfully.

###### Public Methods
Constructor | Description
--- | ---
[`onCallback(PasswordVaultBottomSheetDialog, int)`](#onCallback) | Method invoked for a callback.

<br/>

***

## RESULT_SUCCESS
```java
public final int RESULT_SUCCESS = 0;
```
Result code to use when the dialog closes successfully.

<br/>

## RESULT_CANCEL
```java
public final int RESULT_CANCEL = 2;
```
Result code to use when the dialog closes unsuccessfully.

<br/>

***

## onCallback
```java
public void onCallback(PasswordVaultBottomSheetDialog dialog, int resultCode)
```
Method is called whenever a dialog callback is invoked.

###### Parameters
Parameter | Description
--- | ---
`dialog` | Dialog for which the callback is invoked.
`resultCode` | Result code for the dialog. This is either [RESULT_SUCCESS](#result_success) or [RESULT_CANCEL](#result_cancel).

<br/>

***
2024-12-06  
&copy; Christian-2003
