package jp.techacademy.kanji.okuda.qa_app

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import android.support.design.widget.Snackbar
import android.util.Base64  //追加する
import android.widget.ListView
import com.google.firebase.database.*


class FavoriteActivity : AppCompatActivity() {
    private lateinit var mToolbar: Toolbar
    private var mGenre = 0

    // --- ここから ---
    private lateinit var mDataBaseReference: DatabaseReference
    private lateinit var mListView: ListView
    private lateinit var mQuestionArrayList: ArrayList<Question>
    private lateinit var mAdapter: QuestionsListAdapter

    private var mFavoriteRef: DatabaseReference? = null

    //private val mEventListener = object : ChildEventListener {
    private val mEventListener = object : ChildEventListener {
        override fun onChildChanged(p0: DataSnapshot, p1: String?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onChildRemoved(p0: DataSnapshot) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onChildMoved(p0: DataSnapshot, p1: String?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onCancelled(p0: DatabaseError) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
            val favMap = dataSnapshot.value as Map<String, String>
            val genre = favMap["genre"] ?: ""
            val favoriteUid = favMap["favorite"] ?: ""


            mDataBaseReference = FirebaseDatabase.getInstance().reference
            val mFavoriteRef = mDataBaseReference.child("contents").child(genre).child(favoriteUid)

            mFavoriteRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val map = snapshot.value as Map<String, String>
                    val title = map["title"] ?: ""
                    val body = map["body"] ?: ""
                    val name = map["name"] ?: ""
                    val uid = map["uid"] ?: ""
                    val imageString = map["image"] ?: ""
                    val bytes =
                            if (imageString.isNotEmpty()) {
                                Base64.decode(imageString, Base64.DEFAULT)
                            } else {
                                byteArrayOf()
                            }
                    val answerArrayList = ArrayList<Answer>()
                    val answerMap = map["answers"] as Map<String, String>?
                    if (answerMap != null) {
                        for (key in answerMap.keys) {
                            val temp = answerMap[key] as Map<String, String>
                            val answerBody = temp["body"] ?: ""
                            val answerName = temp["name"] ?: ""
                            val answerUid = temp["uid"] ?: ""
                            val answer = Answer(answerBody, answerName, answerUid, key)
                            answerArrayList.add(answer)
                        }
                    }


                    val question = Question(title, body, name, uid, dataSnapshot.key ?: "",
                            genre.toInt(), bytes, answerArrayList)
                    mQuestionArrayList.add(question)
                    mAdapter.notifyDataSetChanged()
                }
            })
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite)
//        mToolbar = findViewById(R.id.toolbar)
//        setSupportActionBar(mToolbar)
        // Firebase
        mDataBaseReference = FirebaseDatabase.getInstance().reference

        // ListViewの準備
//        mListView = findViewById(R.id.listView)
//        mAdapter = QuestionsListAdapter(this)
//        mQuestionArrayList = ArrayList<Question>()
//        mAdapter.notifyDataSetChanged()
        // --- ここまで追加する ---

//        val user = FirebaseAuth.getInstance().currentUser
//        mFavoriteRef = mDataBaseReference.child(FavoritePATH).child(user!!.uid)
//        mFavoriteRef!!.addChildEventListener(mEventListener)

    }

    override fun onResume() {
        super.onResume()
        // ListViewの準備
        mListView = findViewById(R.id.listView)
        mAdapter = QuestionsListAdapter(this)
        mQuestionArrayList = ArrayList<Question>()
        mQuestionArrayList.clear()
        mAdapter.setQuestionArrayList(mQuestionArrayList)
        mListView.adapter = mAdapter

        mAdapter.notifyDataSetChanged()
        // --- ここまで追加する ---

        val user = FirebaseAuth.getInstance().currentUser
        mFavoriteRef = mDataBaseReference.child(FavoritePATH).child(user!!.uid)
        mFavoriteRef!!.addChildEventListener(mEventListener)

        mListView.setOnItemClickListener { parent, view, position, id ->
            // Questionのインスタンスを渡して質問詳細画面を起動する
            val intent = Intent(applicationContext, QuestionDetailActivity::class.java)
            intent.putExtra("question", mQuestionArrayList[position])
            startActivity(intent)
        }

    }
}











