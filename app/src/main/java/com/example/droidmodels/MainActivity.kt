package com.example.droidmodels

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Space
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.droidmodels.models.SQLColumn
import com.example.droidmodels.models.SQLDatabase
import com.example.droidmodels.models.SQLModel
import com.example.droidmodels.models.Document
import com.example.droidmodels.models.SQLPrimaryColumn
import java.util.Optional
import kotlin.reflect.KClass

fun <T: Any, U: Any> T?.cast(default: U): U {
    if (this==null) return default
    return this as U
}

data class Post(
    var url: String,
    var title: String,
    var data: String,
    var categories: String,
    var liked: Boolean,
    var inReadingList: Boolean,
    var createdDate: String,
    var imageUrl: String,
): Document() {

    object Counter {
        var count = 0
        var index = 0
    }

    object Shared {
       private var posts = mutableListOf<Post>()
       fun add(post: Post){
           posts.add(post)
       }
        fun getAll(): List<Post> {
            return posts
        }

    }
        companion object Model: SQLModel<Post>("post", Post::class) {
            override val id = SQLPrimaryColumn("id", Post::id)

            val url = SQLColumn("url", Post::url)
            val title = SQLColumn("title", Post::title)
            val data = SQLColumn("data", Post::data)
            val categories = SQLColumn("categories", Post::categories)
            val liked = SQLColumn("liked", Post::liked)
            val inReadingList = SQLColumn("in_reading_list", Post::inReadingList)
            val createdDate = SQLColumn("create_date", Post::createdDate)
            val imageUrl = SQLColumn("image_url", Post::imageUrl)

            override fun fromMap(data: Map<String, Any?>): Post {
                val post = Post(
                    url=data["url"].cast(""),
                    title=data["title"].cast(""),
                    data=data["data"].cast(""),
                    categories=data["categories"].cast(""),
                    liked=data["liked"].cast(false),
                    inReadingList=data["inReadingList"].cast(false),
                    createdDate=data["createdDate"].cast(""),
                    imageUrl=data["imageUrl"].cast(""),
                )
                val itemId = data["id"]
                if (itemId != null){
                   post.id = (itemId as Int).toLong()
                }
                //post.id = data["id"] as Long
                println("\n$post\n")
                return post
            }

        fun default(): Post {
            return Post(
                url="https://google.com@${Counter.count}",
                title="this is title ${Counter.count}",
                data="{key: value#${Counter.count}}",
                categories="[1, 3, 4]",
                liked=Counter.count%3 ==0 ,
                inReadingList=Counter.count%2 == 0,
                createdDate="date #${Counter.count}",
                imageUrl="http://image_${Counter.count}.png",
            ).let {
                Counter.count++
                it
            }
        }
    }

    fun toView(context: Context, nColumns:Int=2): LinearLayout {

        val linearLayout = LinearLayout(context)
        linearLayout.orientation = LinearLayout.VERTICAL
        linearLayout.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
        )
        val getTextView = { text: String ->
            val textView = TextView(context)
            textView.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
            )
            textView.text = text
            textView
        }
        val items = mutableListOf<View>()
        val index = Counter.index

        val addRow = { views: List<View> ->
            val tempLinearLayout = LinearLayout(context)
            tempLinearLayout.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                100,
//                ViewGroup.LayoutParams.WRAP_CONTENT,
            )
            tempLinearLayout.setBackgroundColor(Color.WHITE)
            tempLinearLayout.addView(getTextView("$index"))
            views.forEach{ tempLinearLayout.addView(it) }
            val scrollView = ScrollView(context)
            scrollView.addView(tempLinearLayout)
            linearLayout.addView(scrollView)
            val spacer = Space(context)
            spacer.layoutParams = ViewGroup.LayoutParams(
                39,
                20,
            )
            spacer.setBackgroundColor(Color.RED)
            linearLayout.addView(spacer)
        }

        var fcount = 0
        for (field in this.getMemberNames()){
            val value = this.getMember<Any>(field)
            Log.d("APP", "${fcount++} -> ${field}: ${value}")
            val textView = getTextView(" ${field}: ${value} | ")

            if (items.size>0 && items.size%nColumns==0){
                addRow(items)
                items.clear()
            } else {
                items.add(textView)
            }
        }
        if (items.size> 0 ){
            addRow(items)
        }
        Counter.index++
        val spacer = Space(context)
        spacer.layoutParams = ViewGroup.LayoutParams(
            39,
            80,
        )
        spacer.setBackgroundColor(Color.RED)
        linearLayout.addView(spacer)
        return linearLayout
    }
}


class MainActivity: AppCompatActivity() {

    @SuppressLint("AppCompatMethod")
    override fun onCreate(savedInstanceState:Bundle?) {
        super.onCreate(savedInstanceState)
        actionBar.ifNotNull { it.hide() }
        setContentView(R.layout.main)

        val db = SQLDatabase(this, null, "db", 1)
        db.addModel(Post)

        val searchView = LinearLayout::class.fromResourceId(this, R.layout.searchview)
        val dbResultsView = LinearLayout::class.fromResourceId(this, R.layout.db_result_view)
        dbResultsView.layoutParams = ViewGroup.LayoutParams(
            900,
            1800,
        )
        val container = findViewById<LinearLayout>(R.id.container)
        val switchSearchBtn = findViewById<Button>(R.id.do_search)
        val switchViewDBBtn = findViewById<Button>(R.id.view_db)

        var isInSearchView = true

        switchSearchBtn.setOnClickListener {
            if (!isInSearchView){
                container.removeAllViews()
                container.addView(searchView)
                isInSearchView = true

                val submitBtn = findViewById<Button>(R.id.submit)
                submitBtn.setOnClickListener {
                    val url=findViewById<EditText>(R.id.url).text.toString()
                    val title=findViewById<EditText>(R.id.title).text.toString()
                    val data=findViewById<EditText>(R.id.data).text.toString()
                    val categories=findViewById<EditText>(R.id.categories).text.toString()
                    val liked= false
                    val inReadingList= false
                    val createdDate="date #${Post.Counter.count}"
                    val imageUrl=findViewById<EditText>(R.id.imageUrl).text.toString()

//                    if (listOf(url, title, categories, data, imageUrl).any { url.isBlank() }){
//                        Toast.makeText(this, "Fields must not be blank!", Toast.LENGTH_SHORT).show()
//                        return@setOnClickListener
//                    }
                    val success = Post.add(
                        Post(
                            url=url,
                            title=title,
                            data=data,
                            categories=categories,
                            liked= liked,
                            inReadingList=inReadingList,
                            createdDate=createdDate,
                            imageUrl=imageUrl,
                        )
                    )
                    Toast.makeText(this, if (success) "Success" else "Failed", Toast.LENGTH_SHORT).show()
                    container.removeAllViews()
                    container.addView(searchView)
                }

            }
        }

        switchViewDBBtn.setOnClickListener {
            if (isInSearchView){
                container.removeAllViews()
                container.addView(dbResultsView)
                isInSearchView = false

                val postContainer = findViewById<LinearLayout>(R.id.post_container)
                val clearPostsButton = findViewById<Button>(R.id.clear_posts)

                val posts = mutableListOf<Pair<Post, View>>()
                clearPostsButton.setOnClickListener {
                    posts.forEach {
                        if (Post.delete(it.first.id)){
                            postContainer.removeView(it.second)
                        }
                    }
//                    postContainer.removeAllViews()
                }

                Post.getAll().ifPresent {
                    it.forEach {post ->
                        val view = post.toView(this)
                        posts.add(Pair(post, view))
                        postContainer.addView(view)

                    }
                }

//                randomPostBtn.setOnClickListener {
//                    postContainer.removeAllViews()
//                }

            }
        }

    }

}

fun<T: View> KClass<T>.fromResourceId(context: Context, id: Int): T {
    val inflater = LayoutInflater.from(context)
    return inflater.inflate(id, null, false) as T
}

fun <T> T?.ifNotNull(f: (T)->Unit){
    if (this != null) f(this)
}

fun <T> Optional<T>.ifPresent(f: (T)->Unit){
    if (this.isPresent) f(this.get())
}
//fun <T: KClass<View>> T.fromResourceId(context: Context, id: Int): View {
//    val inflater = LayoutInflater.from(context)
//    return inflater.inflate(id, null, false)
//}