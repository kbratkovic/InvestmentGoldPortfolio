package com.kbratkovic.investmentgoldportfolio.util


import android.content.Context
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import com.kbratkovic.investmentgoldportfolio.R
import androidx.recyclerview.widget.RecyclerView


abstract class SwipeToDeleteCallback internal constructor(context: Context) : ItemTouchHelper.Callback() {

    private var mContext: Context
    private val mClearPaint: Paint
    private val mBackground: ColorDrawable
    private val mBackgroundColor: Int
    private val mDeleteDrawable: Drawable?
    private val mIntrinsicWidth: Int
    private val mIntrinsicHeight: Int

    init {
        mContext = context
        mBackground = ColorDrawable()
        mBackgroundColor = Color.parseColor("#b80f0a")
        mClearPaint = Paint()
        mClearPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        mDeleteDrawable = ContextCompat.getDrawable(mContext, R.drawable.ic_baseline_delete_24)
        mIntrinsicWidth = mDeleteDrawable!!.intrinsicWidth
        mIntrinsicHeight = mDeleteDrawable.intrinsicHeight
    }

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        return makeMovementFlags(0, ItemTouchHelper.LEFT)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        viewHolder1: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onChildDraw(
        canvas: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        val itemView: View = viewHolder.itemView
        val itemHeight: Int = itemView.height
        val isCancelled = dX == 0f && !isCurrentlyActive
        if (isCancelled) {
            clearCanvas(
                canvas,
                itemView.right + dX,
                itemView.top.toFloat(),
                itemView.right.toFloat(),
                itemView.bottom.toFloat()
            )
            super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            return
        }
        mBackground.color = mBackgroundColor
        mBackground.setBounds(
            itemView.right + dX.toInt(),
            itemView.top,
            itemView.right,
            itemView.bottom
        )
        mBackground.draw(canvas)
        val deleteIconTop: Int = itemView.top + (itemHeight - mIntrinsicHeight) / 2
        val deleteIconMargin = (itemHeight - mIntrinsicHeight) / 2
        val deleteIconLeft: Int = itemView.right - deleteIconMargin - mIntrinsicWidth
        val deleteIconRight: Int = itemView.right - deleteIconMargin
        val deleteIconBottom = deleteIconTop + mIntrinsicHeight
        mDeleteDrawable!!.setBounds(deleteIconLeft, deleteIconTop, deleteIconRight, deleteIconBottom)
        mDeleteDrawable.draw(canvas)
        super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    private fun clearCanvas(c: Canvas, left: Float, top: Float, right: Float, bottom: Float) {
        c.drawRect(left, top, right, bottom, mClearPaint)
    }

    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
        return 0.7f
    }
}