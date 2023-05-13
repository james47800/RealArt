package com.mwangi.real.adapters

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.signature.ObjectKey
import com.mwangi.real.R
import com.mwangi.real.models.Arts
import java.util.*
import kotlin.collections.ArrayList

class ArtsAdapter(var context: Context, var data: ArrayList<Arts>) : BaseAdapter() {

    private class ViewHolder(row: View?) {
        var imgArt: ImageView
        var btnShare: ImageView
        var txtUserDescription: TextView

        init {
            this.imgArt = row?.findViewById(R.id.mImgArt) as ImageView
            this.btnShare = row?.findViewById(R.id.shareIcon) as ImageView
            this.txtUserDescription = row?.findViewById(R.id.User_description) as TextView
        }
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view: View?
        var viewHolder: ViewHolder
        if (convertView == null) {
            var layout = LayoutInflater.from(context)
            view = layout.inflate(R.layout.art_layout, parent, false)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }
        var item: Arts = getItem(position) as Arts
        Glide.with(context)
            .load(item.imageUrl)
            .signature(ObjectKey(item.imageUrl)) // set unique signature
            .transform(CenterCrop(), RoundedCorners(16)) // apply transformations
            .into(viewHolder.imgArt)

        viewHolder.txtUserDescription.text = item.userDescription

        viewHolder.btnShare.setOnClickListener {
            // Create a sharing intent with the image URI
            val imageUri = Uri.parse(item.imageUrl)
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            shareIntent.type = "image/*"
            shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri)

            // Launch the sharing dialog
            val chooser = Intent.createChooser(shareIntent, "Share art using")
            chooser.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(chooser)
        }
        return view as View
    }

    override fun getItem(position: Int): Any {
        return data[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return data.size
    }
    fun setSearchOperation(newList: ArrayList<Arts>) {
        data = ArrayList()
        data.addAll(newList)
        notifyDataSetChanged()
    }

//    private fun getLevenshteinDistance(s: String, t: String): Int {
//        val m = s.length
//        val n = t.length
//
//        if (m == 0) {
//            return n
//        }
//
//        if (n == 0) {
//            return m
//        }
//
//        val d = Array(m + 1) { IntArray(n + 1) }
//
//        for (i in 0..m) {
//            d[i][0] = i
//        }
//
//        for (j in 0..n) {
//            d[0][j] = j
//        }
//
//        for (j in 1..n) {
//            for (i in 1..m) {
//                if (s[i - 1] == t[j - 1]) {
//                    d[i][j] = d[i - 1][j - 1]
//                } else {
//                    d[i][j] = 1 + minOf(d[i - 1][j], d[i][j - 1], d[i - 1][j - 1])
//                }
//            }
//        }
//
//        return d[m][n]
//    }

}
