package com.test.myapplication

import android.animation.ArgbEvaluator
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs
import kotlin.math.min

class FirstFragment : Fragment(R.layout.fragment_main) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.addItemDecoration(DividerItemDecoration(requireContext(), RecyclerView.VERTICAL))

        val adapter = RecyclerAdapter()
        recyclerView.adapter = adapter

        val callback = ItemTouchHelperCallback()
        ItemTouchHelper(callback).attachToRecyclerView(recyclerView)
    }


    class RecyclerAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return SwipeViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false))
        }

        override fun getItemCount(): Int {
            return 10
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        }
    }


    class ItemTouchHelperCallback : ItemTouchHelper.Callback() {

        override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
            return makeMovementFlags(0, ItemTouchHelper.START)
        }

        override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

        }

        override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
            getDefaultUIUtil().clearView((viewHolder as SwipeViewHolder).containerView)
        }

        override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
            (viewHolder as SwipeViewHolder)

            // Sets the elevation of the 'container view' as well as its translation
            getDefaultUIUtil().onDraw(c, recyclerView, viewHolder.containerView, dX, dY, actionState, isCurrentlyActive)

            // dX is negative when swiping to the left. Convert to positive
            val absDx = abs(dX)

            // The amount of swipe required to start our transition
            val start = 64f.dp

            // The length of swiping over which the transition will occur
            val length = 64f.dp

            when {
                absDx < start -> {
                    viewHolder.archiveButton.imageTintList = ColorStateList.valueOf(Color.BLACK)
                }
                absDx > (start + length) -> {
                    // If we've moved past our threshold, set the background to match our paint color (blue)
                    // Note: We use a clipping rect so our colour doesn't show up in the gaps
                    c.clipRect(RectF(viewHolder.itemView.width + dX, viewHolder.itemView.y, viewHolder.itemView.x + viewHolder.itemView.width, viewHolder.itemView.y + viewHolder.itemView.height))
                    c.drawColor(viewHolder.paint.color)

                    viewHolder.archiveButton.imageTintList = ColorStateList.valueOf(Color.WHITE)
                }
                else -> {
                    // The ratio of current swipe over our transition distance
                    val ratio = (absDx - start) / length

                    // Clipping rect: See above
                    c.clipRect(RectF(viewHolder.itemView.width + dX, viewHolder.itemView.y, viewHolder.itemView.x + viewHolder.itemView.width, viewHolder.itemView.y + viewHolder.itemView.height))

                    // We draw a circle whose radius changes with our ratio
                    c.drawCircle(
                        viewHolder.itemView.x + viewHolder.archiveButton.x + viewHolder.archiveButton.width / 2,
                        viewHolder.itemView.y + viewHolder.archiveButton.y + viewHolder.archiveButton.height / 2,
                        recyclerView.width * ratio,
                        viewHolder.paint
                    )

                    // Change the button colour from black to white. We're using '5x our ratio' so this transition occurs quickly
                    viewHolder.archiveButton.imageTintList = ColorStateList.valueOf(ArgbEvaluator().evaluate(min(1f, ratio * 5), Color.BLACK, Color.WHITE) as Int)
                }
            }
        }

        override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
            if (viewHolder != null) {
                getDefaultUIUtil().onSelected((viewHolder as SwipeViewHolder).containerView)
            }
        }
    }
}

class SwipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val containerView: View = itemView.findViewById(R.id.containerView)
    val archiveButton: ImageView = itemView.findViewById(R.id.archiveButton)

    // Create our paint here, so we don't have to instantiate each time onDraw() is called
    val paint = Paint().apply {
        color = 0xFF3F51B5.toInt()
        style = Paint.Style.FILL
    }
}

val Float.dp: Float get() = (this * Resources.getSystem().displayMetrics.density)