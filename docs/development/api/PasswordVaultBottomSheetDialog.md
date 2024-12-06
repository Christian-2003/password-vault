<img src="../../img/icon.png" height="150" align="right"/>

# PasswordVaultBottomSheetDialog
```java
public class PasswordVaultBottomSheetDialog<V extends ViewModel> extends BottomSheetDialogFragment
```
Base class for all bottom sheet dialogs within Password Vault.

###### Generic Types
Generic Type | Description
--- | ---
`V extends ViewModel` | Type of the view model from which the dialog sources it's data.

###### Table of Contents
1. [Overview](#overview)
2. [Nested Types](#nested-types)
3. [Public Fields](#public-fields)
4. [Protected Fields](#protected-fields)
5. [Public Constructors](#public-constructors)

<br/>

## Overview
This bottom sheet dialog provides basic functionality that needs to be handled by all dialogs within the app. Extend this dialog and override required methods:
```java
public class MyDialog extends PasswordVaultBottomSheetDialog<MyViewModel> {

    public MyDialog() {
        super(MyViewModel.class, R.layout.dialog_my)
    }


    @Override
    @NonNull
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        if (view == null) {
            return null;
        }

        //Custom setup for the view

        return view;
    }

}
```

Use the dialog in your Java activities like this:
```java
public class MyActivity extends PasswordVaultActivity<MyActivityViewModel> {

    public MyActivity() {
        super(MyActivityViewModel.class, R.layout.activity_my);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MyDialog dialog = new MyDialog();
        dialog.show(getSupportFragmentManager(), null);
    }

}
```

<br/>

## Summary

###### Nested Types
Nested Type | Description
--- | ---
[`Callback`](PasswordVaultBottomSheetDialog.Callback.md) | Callback for the dialog.

###### Protected Fields
Field | Description
--- | ---
[`viewModel`](#viewmodel) | View model from which the dialog can source it's data.
[`callback`](#callback) | Callback to invoke when the dialog closes.

###### Public Constructors
Constructor | Description
--- | ---
[`PasswordVaultBottomSheetDialog(Class<V>, int)`](#recyclerviewadapter) | Constructor to create a new recycler view adapter.
[`PasswordVaultBottomSheetDialog(Class<V>, int, Callback)`](#recyclerviewadapter) | Constructor to create a new recycler view adapter for the specified callback.

###### Public Methods
Method | Description
--- | ---
[`onCreateView(LayoutInflater, ViewGroup, Bundle)`](#onCreateView) | Method is called whenever the view for the dialog is created.
[`onCreateDialog(Bundle)`](#onCreateDialog) | Method is called whenever the dialog is created.
[`onAttach(Context)`](#onAttach) | Method is called whenever the dialog is attached to a context.

<br/>

***

## viewModel
```java
protected V viewModel;
```
View model from which the dialog can source it's data. This can be `null` if argument `null` is passed to constructor as view model type.

<br/>

## callback
```java
@Nullable
protected Callback callback;
```
Callback to invoke once the dialog closes. This can be `null` if the dialog host does not implement the [Callback](PasswordVaultBottomSheetDialog.Callback.md)-interface and the constructor is not called with an instance for this callback.

<br/>

***

## RecyclerViewAdapter
```java
public PasswordVaultBottomSheetDialog(@Nullable Class<V> viewModelType, @LayoutRes int layoutRes)

public PasswordVaultBottomSheetDialog(@Nullable Class<V> viewModelType, @LayoutRes int layoutRes, @Nullable Callback attachableCallback)
```
Constructor instantiates a new bottom sheet dialog handling a view model of the specified type. Furthermore, the dialog displays the view defined in the specified layout resource.
If the host of the dialog does not implement the `Callback` interface or the callback of the host should not be used with the dialog, an attachable callback can be passed to the constructor which takes precedence.

###### Parameters
Parameter | Description
--- | ---
`viewModelType` | Type of the view model from which the adapter should source it's data. This can be `null` if the dialog does not require a view model.
`layoutRes` | ID of the layout resource for the dialog to display.
`attachableCallback` | Optional callback that takes precendence over the dialog host.

<br/>

***

## onAttachedToRecyclerView
```java
@Nullable
@Override
public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
```
Method is called whenever the view for the dialog is created.

###### Parameters
Parameter | Description
--- | ---
`inflater` | Layout inflater to use when inflating the layout for the dialog.
`container` | Parent view group for the dialog.
`savedInstanceState` | Previously saved state of the instance.

###### Returns
Inflated view for the dialog.

<br/>

## onCreateDialog
```java
@Override
public Dialog onCreateDialog(Bundle savedInstanceState)
```
Method is called whenever the dialog is created.

###### Parameters
Parameter | Description
--- | ---
`savedInstanceState` | Previously saved state of the instance.

###### Returns
Created dialog.

<br/>

## onAttach
```java
@Override
public void onAttach(Context context)
```
Method is called whenever the dialog is attached to a context.

###### Parameters
Parameter | Description
--- | ---
`context` | Context to which to attach the dialog.

<br/>

***
2024-12-06  
&copy; Christian-2003
