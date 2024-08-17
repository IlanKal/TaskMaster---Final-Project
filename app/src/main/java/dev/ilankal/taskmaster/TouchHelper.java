package dev.ilankal.taskmaster;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import dev.ilankal.taskmaster.Adapter.TaskAdapter;
import dev.ilankal.taskmaster.Utilities.SoundPlayer;
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class TouchHelper extends ItemTouchHelper.SimpleCallback {
    private SoundPlayer soundPlayer;
    private TaskAdapter taskAdapter;
    private Context context;

    public TouchHelper(TaskAdapter taskAdapter, Context context) {
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.taskAdapter = taskAdapter;
        this.context = context;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        final int pos = viewHolder.getBindingAdapterPosition();

        // Initialize the SoundPlayer
        soundPlayer = new SoundPlayer(context);

        if (direction == ItemTouchHelper.RIGHT) {
            soundPlayer.playSound(R.raw.delete_sound, false);
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("Are you sure?")
                    .setPositiveButton("Yes", (dialog, which) -> taskAdapter.deleteTask(pos))
                    .setNegativeButton("No", (dialog, which) -> taskAdapter.notifyItemChanged(pos));
            AlertDialog dialog = builder.create();
            dialog.show();
        } else if (direction == ItemTouchHelper.LEFT) {
            taskAdapter.editTask(pos);
        }
    }


    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder,
                            float dX, float dY, int actionState, boolean isCurrentlyActive) {
        new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                .addSwipeRightActionIcon(R.drawable.baseline_clear_24)
                .addSwipeRightBackgroundColor(Color.RED)
                .addSwipeLeftActionIcon(R.drawable.baseline_edit_24)
                .addSwipeLeftBackgroundColor(ContextCompat.getColor(context, R.color.malibu))
                .create().decorate();
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }
}
