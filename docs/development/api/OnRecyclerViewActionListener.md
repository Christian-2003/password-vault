<img src="../../img/icon.png" height="150" align="right"/>

# OnRecyclerViewActionListener
```java
public interface OnRecyclerViewActionListener
```
Functional interface that is used with any actions that are invoked from recycler view items.

###### Table of Contents
1. [Overview](#overview)
2. [Public Methods](#public-methods)

<br/>

## Overview
This functional interface provides a way to listen for actions within a recycler view. This eliminates the need to invoke code from the adapter and move such code invokation to the host activity. This results in very clean code.

The following example implements a recycler view adapter for a recycler view which contains a single item - a generic headline button. When the headline button is clicked, an action is invoked using an `OnRecyclerViewActionListener`. To do so, implement the recycler view as follows:
```java
public class MyAdapter extends RecyclerViewAdapter<MyViewModel> {

    @Nullable
    private MyAdapter actionListener;


    public MyRecyclerViewAdapter(@NonNull Context context, @NonNull MyViewModel viewModel, @Nullable OnRecyclerViewActionListener actionListener) {
        super(context, viewModel);
        this.actionListener = actionListener;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(View parent, int viewType) {
        View item = layoutInflater.inflate(R.layout.item_generic_headline_button, parent, false);
        return new GenericHeadlineButtonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        GenericHeadlineButtonViewHolder viewHolder = (GenericHeadlineButtonViewHolder)holder;
        viewHolder.headlineTextView.setText("Click here to invoke action");
        viewHolder.itemView.setOnClickListener(view -> {
            if (actionListener != null) {
                // Important: Use holder.getAdapterPosition() instead of position parameter, so that the passed
                // position is valid even if other items are inserted before this item after this method
                // is called!
                actionListener.invoke(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return 1;
    }

}
```

In your activity, use the following code:
```java
public class MyActivity extends PasswordVaultActivity<MyViewModel> {

    public MyActivity() {
        super(MyViewModel.class, R.layout.activty_my);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        MyAdapter adapter = new MyAdapter(this, viewModel, this::actionInvoked);
        recyclerView.setAdapter(adapter);
    }


    private void actionInvoked(int position) {
        // Replace this with your custom action.
        Toask.makeText(this, "Action invoked", Toast.LENGTH_SHORT).show();
    }

}
```

Once the code is compiled, clicking the generic headline button results in a `Toast` being displayed from the host activity.

<br/>

## Summary
###### Public Methods
Method | Description
[`onAction(int)`](#onaction) | Method called when any action is invoked.

<br/>

***

## onAction
```java
void onAction(int position);
```
Method is called when any action is invoked within a recycler view. The position of the item that invoked the action is called as parameter. This position does not correlate to any specific position within the data set, but rather the position of the calling view within the adapter. Converting this position into a position within a specific data set must be done manually.

###### Parameters
Parameter | Description
--- | ---
`position` | Position of the calling view within the adapter.

<br/>

***
2024-08-25  
&copy; Christian-2003
