package com.example.wiberdriver.fragments

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.wiberdriver.R
import com.example.wiberdriver.viewmodels.SignInViewModel
import com.google.android.material.textfield.TextInputLayout

class SignInFragment : Fragment() {

    private lateinit var loginviewModel: SignInViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        loginviewModel = ViewModelProvider(this).get(SignInViewModel::class.java)

        val root = inflater.inflate(R.layout.fragment_sign_in, container, false)

        val phoneNumberTextField = root.findViewById<TextInputLayout>(R.id.phoneNumberInputText)
        val passwordTextField = root.findViewById<TextInputLayout>(R.id.paswordInputText)

        loginviewModel.phoneNumberText.observe(viewLifecycleOwner){
            phoneNumberTextField.editText?.setText(it)
        }

        loginviewModel.passwordText.observe(viewLifecycleOwner){
            passwordTextField.editText?.setText(it)
        }


        return root
    }


}