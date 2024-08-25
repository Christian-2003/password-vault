<img src="../../img/icon.png" height="150" align="right"/>

# RecyclerViewSwipeCallback
```java
public class RecyclerViewSwipeCallback extends ItemTouchHelper.Callback
```
Class models a swipe callback that supports item swiping with a [`RecyclerViewAdapter`](RecyclerViewAdapter.md).

###### Table of Contents
1. [Overview](#overview)
2. [Nested Types](#nested-types)
3. [Public Constructors](#public-constructors)
4. [Public Methods](#public-methods)

<br/>

## Overview
This class provides a convenient callback useable to enable swiping within a recycler view adapter.

To use this callback, prepare your recycler view adapter as follows:
```java
// Your adapter must implement the interface RecyclerViewSwipeCallback.SwipeContract
public class MyAdapter extends RecyclerViewAdapter<MyViewModel> implements RecyclerViewSwipeCallback.SwipeContract {

    public MyAdapter(@NonNull Context context, @NonNull MyViewModel viewModel) {
        super(context, viewModel);
    }


    // Implement required methods


    @Override
    public boolean supportsSwipe(int position) {
        // Custom logic to determine whether the position passed as argument supports
        // swiping. Returning true without custom logic results in all items being
        // swipeable.
        return true;
    }
}
```

Use the callback in your activity as follows:
```java
public class MyActivity extends PasswordVaultActivity<MyViewModel> {

    public MyActivity() {
        super(MyViewModel.class, R.layout.activity_my);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        MyAdapter adapter = new MyAdapter(this, viewModel);

        // Create swipe actions
        RecyclerViewSwipeCallback.SwipeAction leftSwipe = RecyclerViewSwipeCallback.makeLeftSwipeAction(this::onEdit, this::onDelete);
        RecyclerViewSwipeCallback.SwipeAction rightSwipe = RecyclerViewSwipeCallback.makeRightSwipeAction(this::onEdit, this::onDelete);

        //Create swipe callback:
        RecyclerViewSwipeCallback callback = new RecyclerViewSwipeCallback(adapter, leftSwipe, rightSwipe);
        
        //Attach callback to recycler view:
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);
    }


    private void onEdit(int position) {
        // Your code to edit an item goes here.
    }

    private void onDelete(int position) {
        // Your code to delete an item goes here.
    }

}
```

<br/>

## Summary
###### Nested Types
Nested Type | Description
--- | ---
[`SwipeContract`](RecyclerViewSwipeCallback.SwipeContract.md) | Interface for a recycler view to implement in order to support swipe actions.
[`SwipeAction`](RecyclerViewSwipeCallback.SwipeAction.md) | Configuration for a swipe action.

###### Public Constructors
Constructor | Description
--- | ---
[`RecyclerViewSwipeCallback(SwipeContract, SwipeAction, SwipeAction)`](#recyclerviewswipecallback-1) | Constructor instantiates a new swipe callback with the specified swipe actions.

###### Public Methods
Method | Description
--- | ---
[`makeLeftSwipeAction(OnRecyclerViewActionListener, OnRecyclerViewActionListener)`](#makeleftswipeaction) | Generates the left swipe action according to the app configuration.
[`makeRightSwipeAction(OnRecyclerViewActionListener, OnRecyclerViewActionListener)`](#makerightswipeaction) | Generates the right swipe action according to the app configuration.

<br/>

***

## RecyclerViewSwipeCallback
```java
public RecyclerViewSwipeCallback(
    @NonNull SwipeContract adapter,
    @Nullable SwipeAction leftSwipeAction,
    @Nullabke SwipeAction rightSwipeAction
)
```
Constructor instantiates a new swipe callback for the specified adapter. When the user swipes left or right, the corresponding swipe actions will be used. If a swipe action is `null`, the corresponding swipe will not be supported by the callback.

###### Parameters
Parameter | Description
--- | ---
`adapter` | Adapter for which this callback is created.
`leftSwipeAction` | Swipe action used when the user swipes left.
`rightSwipeAction` | Swipe action used when the user swipes right.

<br/>

***

## makeLeftSwipeAction
```java
public static SwipeAction makeLeftSwipeAction(
    @Nullable OnRecyclerViewActionListener editListener,
    @Nullable OnRecyclerViewActionListener deleteListener
)
```
Method creates the swipe action used when the user swipes left. The swipe action will be configured according to the app configuration, which only supports swiping to edit and delete.

###### Parameters
Parameter | Description
--- | ---
`editListener` | Action to invoke when the user swipes to edit.
`deleteListener` | Action to invoke when the user swipes to delete.

###### Returns
Swipe action to use when left swiping.

<br/>

## makeRightSwipeAction
```java
public static SwipeAction makeRightSwipeAction(
    @Nullable OnRecyclerViewActionListener editListener,
    @Nullable OnRecyclerViewActionListener deleteListener
)
```
Method creates the swipe action used when the user swipes right. The swipe action will be configured according to the app configuration, which only supports swiping to edit and delete.

###### Parameters
Parameter | Description
--- | ---
`editListener` | Action to invoke when the user swipes to edit.
`deleteListener` | Action to invoke when the user swipes to delete.

###### Returns
Swipe action to use when right swiping.

<br/>

***

2024-08-25  
&copy; Christian-2003
