package com.example.samgram.adapters

import android.annotation.SuppressLint
import android.text.Html
import android.text.Spanned
import android.text.format.DateUtils
import android.view.*
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.recyclerview.widget.RecyclerView
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.GestureDetectorCompat
import com.bumptech.glide.Glide
import com.example.samgram.R
import com.example.samgram.Utils
import com.example.samgram.modal.Feed
import de.hdodenhof.circleimageview.CircleImageView
import java.text.SimpleDateFormat
import java.util.*


class MyFeedAdapter : RecyclerView.Adapter<FeedHolder>() {


     var feedlist = listOf<Feed>()
     private  var listener : onDoubleTapClickListener ?=  null


    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.feeditem, parent, false)


        return FeedHolder(view)


    }

    override fun getItemCount(): Int {

        return feedlist.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: FeedHolder, position: Int) {

        val feed = feedlist[position]

        val boldText = "<b>${feed.username}</b>"
        val additionalText = ": ${feed.caption}"
        val combinedText = boldText + additionalText
        val formattedText: Spanned = Html.fromHtml(combinedText)


        holder.userNameCaption.text = formattedText



        val date = Date(feed.time!!.toLong() * 1000)

        val instagramTimeFormat = DateUtils.getRelativeTimeSpanString(
            date.time,
            System.currentTimeMillis(),
            DateUtils.MINUTE_IN_MILLIS,
            DateUtils.FORMAT_ABBREV_RELATIVE
        )

        holder.time.setText(instagramTimeFormat)
        holder.userNamePoster.setText(feed.username)

        Glide.with(holder.itemView.context).load(feed.image).into(holder.feedImage)
        Glide.with(holder.itemView.context).load(feed.imageposter).into(holder.userPosterImage)

        holder.likecount.setText("${feed.likes} Likes")


//        holder.itemView.setOnClickListener {
//            listener?.onDoubleTap(feed)
//        }

        val doubleClickGestureDetector = GestureDetectorCompat(holder.itemView.context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onDoubleTap(e: MotionEvent): Boolean {
                listener?.onDoubleTap(feed)
                return true
            }
        })

        holder.itemView.setOnTouchListener { _, event ->
            doubleClickGestureDetector.onTouchEvent(event)
            true
        }



    }


    fun setFeedList(list: List<Feed>){
        this.feedlist = list
    }

    fun setListener(listener: onDoubleTapClickListener){
        this.listener = listener
    }

}

class FeedHolder(itemView: View) : ViewHolder(itemView){

    val userNamePoster : TextView = itemView.findViewById(R.id.feedtopusername)
    val userNameCaption : TextView = itemView.findViewById(R.id.feedusernamecaption)

    val userPosterImage : CircleImageView = itemView.findViewById(R.id.userimage)
    val feedImage : ImageView = itemView.findViewById(R.id.feedImage)
    val time: TextView = itemView.findViewById(R.id.feedtime)
    val likecount: TextView = itemView.findViewById(R.id.likecount)



}

interface onDoubleTapClickListener{
     fun onDoubleTap(feed: Feed)
}