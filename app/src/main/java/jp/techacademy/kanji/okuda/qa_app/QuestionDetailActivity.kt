package jp.techacademy.kanji.okuda.qa_app

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.ListView

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_question_detail.*

import java.util.HashMap
import android.R.attr.button
import android.provider.MediaStore
import android.util.Log


class QuestionDetailActivity : AppCompatActivity() {

    private lateinit var mQuestion: Question
    private lateinit var mAdapter: QuestionDetailListAdapter
    private lateinit var mAnswerRef: DatabaseReference

    private val mEventListener = object : ChildEventListener {
        override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
            val a=0
            val map = dataSnapshot.value as Map<String, String>

            val answerUid = dataSnapshot.key ?: ""

            for (answer in mQuestion.answers) {
                // 同じAnswerUidのものが存在しているときは何もしない
                if (answerUid == answer.answerUid) {
                    return
                }
            }

            val body = map["body"] ?: ""
            val name = map["name"] ?: ""
            val uid = map["uid"] ?: ""

            val answer = Answer(body, name, uid, answerUid)
            mQuestion.answers.add(answer)
            mAdapter.notifyDataSetChanged()
        }



        override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {

        }

        override fun onChildRemoved(dataSnapshot: DataSnapshot) {

        }

        override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {

        }

        override fun onCancelled(databaseError: DatabaseError) {

        }
    }




    private val mEventListener2 = object : ChildEventListener {
        override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
            var flag1 = 0
         //  val map = dataSnapshot.value as Map<String, String>

            val favoriteUid = dataSnapshot.key ?: ""

            for (favorite in mQuestion.questionUid) {
                // 同じAnswerUidのものが存在しているときは何もしない
                if (favoriteUid == mQuestion.questionUid) {
                    favButton.text = "お気に入り登録"
                    flag1= 1
                }else{
                    favButton.text = "お気に入り解除"
                    flag1 = 2
                }
            }

        }



        override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {

        }

        override fun onChildRemoved(dataSnapshot: DataSnapshot) {

        }

        override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {

        }

        override fun onCancelled(databaseError: DatabaseError) {

        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_question_detail)

        // 渡ってきたQuestionのオブジェクトを保持する
        val extras = intent.extras
        mQuestion = extras.get("question") as Question

        title = mQuestion.title

        // ListViewの準備
        mAdapter = QuestionDetailListAdapter(this, mQuestion)
        listView.adapter = mAdapter
        mAdapter.notifyDataSetChanged()

        val user = FirebaseAuth.getInstance().currentUser
        val dataBaseReference = FirebaseDatabase.getInstance().reference
        val favorite = dataBaseReference.child(FavoritePATH).child(user!!.uid).child(mQuestion.questionUid)
        favorite.addChildEventListener(mEventListener2)
        // ボタンの定義
        val btn: Button
        btn = findViewById<View>(R.id.favButton) as Button
        if (user == null) {
            Log.d("aaa","user=null")
            btn.setVisibility(View.INVISIBLE)

        }else{
            btn.setVisibility(View.VISIBLE)

        }

        favButton.setOnClickListener{
            Log.d("aaa","oshita")
            //mQuestionをfavoriteに追加
            val data = HashMap<String,String>()


            if (favButton.text.toString() == "お気に入り登録") {
                data["favorite"] = mQuestion.questionUid
                data["genre"] = mQuestion.genre.toString()
                favorite.setValue(data)
                //favorite!!.addChildEventListener(mEventListener)
            }else{
                favorite.removeValue()
                favButton.text = "お気に入り登録"
                //お気に入りに入っていればtextchange
            }



        }



        fab.setOnClickListener {
            // ログイン済みのユーザーを取得する
            val user = FirebaseAuth.getInstance().currentUser

            if (user == null) {
                // ログインしていなければログイン画面に遷移させる
                val intent = Intent(applicationContext, LoginActivity::class.java)
                startActivity(intent)
            } else {
                // --- ここから ---
                val intent = Intent(applicationContext, AnswerSendActivity::class.java)
                intent.putExtra("question", mQuestion)
                startActivity(intent)
                // --- ここまで ---
            }
        }

        //mGenreRef = mDatabaseReference.child(ContentsPATH).child(mGenre.toString())
        mAnswerRef = dataBaseReference.child(FavoritePATH).child(mQuestion.questionUid)
        //mAnswerRef = dataBaseReference.child(FavoritePATH).child(user!!.uid).child(mQuestion.questionUid)
        mAnswerRef.addChildEventListener(mEventListener)
    }
}

