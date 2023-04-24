package com.example.smartchatgpt

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var welcomeTextView: TextView
    private lateinit var messageEditText: EditText
    private lateinit var sendButton: ImageButton
    private var messageList = ArrayList<Message>()
    private lateinit var messageAdapter: MessageAdapter
    private val JSON = "application/json; charset=utf-8".toMediaType()
    private val client = OkHttpClient()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        messageList = ArrayList()
        recyclerView = findViewById(R.id.recycler_view)
        welcomeTextView = findViewById(R.id.welcome_text)
        messageEditText = findViewById(R.id.message_edit_text)
        sendButton = findViewById(R.id.send_button)

        messageAdapter = MessageAdapter(messageList)
        recyclerView.adapter = messageAdapter
        val llm = LinearLayoutManager(this)
        llm.stackFromEnd = true
        recyclerView.layoutManager = llm

        sendButton.setOnClickListener {
            val question = messageEditText.text.toString().trim()
            if (question.isEmpty()) {
                Toast.makeText(this, getString(R.string.toast_write_text), Toast.LENGTH_SHORT)
                    .show()
            } else {
                addToChat(question, Message.SEND_BY_ME)
                messageEditText.setText("")
                callAPI(question)
                welcomeTextView.visibility = View.GONE
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun addToChat(message: String, sendBy: String) {
        runOnUiThread {
            messageList.add(Message(message, sendBy))
            messageAdapter.notifyDataSetChanged()
            recyclerView.smoothScrollToPosition(messageAdapter.itemCount)
        }
    }

    private fun addResponse(response: String) {
        messageList.removeAt(messageList.size - 1)
        addToChat(response, Message.SEND_BY_BOT)
    }

    private fun callAPI(question: String) {
        messageList.add(Message(getString(R.string.wait_typing), Message.SEND_BY_BOT))
        val jsonBody = JSONObject()
        try {
            jsonBody.put("model", "text-davinci-003")
            jsonBody.put("prompt", question)
            jsonBody.put("max_tokens", 4000)
            jsonBody.put("temperature", 0)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val body = jsonBody.toString().toRequestBody(JSON)
        val request = Request.Builder()
            .url("https://api.openai.com/v1/completions")
            .header(
                "Authorization",
                "Bearer YOUR_GPT_CHAT_KEY"
            ) //TODO write your key here for use App
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                addResponse("Failed to load response ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) = if (response.isSuccessful) {
                val jsonObject: JSONObject?
                try {
                    jsonObject = JSONObject(response.body!!.string())
                    val jsonArray = jsonObject.getJSONArray("choices")
                    val result = jsonArray.getJSONObject(0).getString("text")
                    addResponse(result.trim())
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            } else {
                addResponse("Failed to load response ${response.body}")
            }
        })
    }
}