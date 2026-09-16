import re

with open("app/src/main/java/com/example/ui/MarketViewModel.kt", "r") as f:
    content = f.read()

imports = """
import android.app.Activity
import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.FirebaseException
import java.util.concurrent.TimeUnit
"""
content = content.replace("import kotlinx.coroutines.launch", "import kotlinx.coroutines.launch\n" + imports)

auth_methods = """
    fun loginOffline(email: String, pass: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val user = dao.getUserByEmail(email.trim())
            if (user != null && user.password == pass) {
                _currentUser.value = user
                when (user.role) {
                    "ADMIN" -> _currentScreen.value = "ADMIN_MAIN"
                    "SELLER" -> _currentScreen.value = "SELLER_MAIN"
                    else -> _currentScreen.value = "BUYER_MAIN"
                }
                onResult(true, "Login successful")
            } else {
                onResult(false, "Invalid email or password")
            }
        }
    }

    private fun handleFirebaseUser(uid: String, email: String?, phone: String?, name: String?, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val dbEmail = email ?: "${uid}@firebase.kashmirmarket.com"
            val dbName = name ?: "User"
            val dbPhone = phone ?: ""
            
            var user = dao.getUserByEmail(dbEmail)
            if (user == null) {
                // Check if user exists by phone if phone is present
                if (dbPhone.isNotEmpty()) {
                    // We don't have getUserByPhone, but we can search for it or just insert.
                    // For simplicity, we just insert.
                }
                
                val newUser = UserEntity(
                    email = dbEmail,
                    password = "", // Firebase managed
                    name = dbName,
                    phone = dbPhone,
                    role = "BUYER",
                    isSellerActive = false
                )
                val id = dao.insertUser(newUser)
                user = dao.getUserById(id)
            }
            if (user != null) {
                _currentUser.value = user
                when (user.role) {
                    "ADMIN" -> _currentScreen.value = "ADMIN_MAIN"
                    "SELLER" -> _currentScreen.value = "SELLER_MAIN"
                    else -> _currentScreen.value = "BUYER_MAIN"
                }
                onResult(true, "Login successful")
            } else {
                onResult(false, "Failed to setup local user account")
            }
        }
    }

    fun loginWithGoogle(context: Context, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                val credentialManager = CredentialManager.create(context)
                val googleIdOption = GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId("123456789012-dummy.apps.googleusercontent.com")
                    .build()
                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()
                
                val result = credentialManager.getCredential(context, request)
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(result.credential.data)
                val googleIdToken = googleIdTokenCredential.idToken
                
                val authCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
                FirebaseAuth.getInstance().signInWithCredential(authCredential).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val fbUser = task.result?.user
                        handleFirebaseUser(fbUser?.uid ?: "", fbUser?.email, fbUser?.phoneNumber, fbUser?.displayName, onResult)
                    } else {
                        onResult(false, task.exception?.message ?: "Google Sign In Failed")
                    }
                }
            } catch (e: GetCredentialException) {
                onResult(false, "Google Sign-In requires google-services.json and Firebase configuration.")
            } catch (e: Exception) {
                onResult(false, e.message ?: "Authentication Failed")
            }
        }
    }

    fun sendPhoneOtp(activity: Activity, phone: String, onCodeSent: (String) -> Unit, onResult: (Boolean, String) -> Unit) {
        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: com.google.firebase.auth.PhoneAuthCredential) {
                FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val fbUser = task.result?.user
                        handleFirebaseUser(fbUser?.uid ?: "", fbUser?.email, fbUser?.phoneNumber, fbUser?.displayName, onResult)
                    } else {
                        onResult(false, task.exception?.message ?: "Phone Auth Failed")
                    }
                }
            }
            override fun onVerificationFailed(e: FirebaseException) {
                onResult(false, "Verification failed: Google-services.json missing or API key invalid.")
            }
            override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                onCodeSent(verificationId)
            }
        }
        val options = PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
            .setPhoneNumber(phone)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)
            .build()
        try {
            PhoneAuthProvider.verifyPhoneNumber(options)
        } catch (e: Exception) {
             onResult(false, "Initialization failed. Check Firebase config.")
        }
    }

    fun verifyPhoneOtp(verificationId: String, code: String, onResult: (Boolean, String) -> Unit) {
        try {
            val credential = PhoneAuthProvider.getCredential(verificationId, code)
            FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val fbUser = task.result?.user
                    handleFirebaseUser(fbUser?.uid ?: "", fbUser?.email, fbUser?.phoneNumber, fbUser?.displayName, onResult)
                } else {
                    onResult(false, task.exception?.message ?: "Invalid OTP")
                }
            }
        } catch (e: Exception) {
             onResult(false, "Verification failed.")
        }
    }

    fun loginWithEmail(email: String, pass: String, onResult: (Boolean, String) -> Unit) {
        try {
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val fbUser = task.result?.user
                    handleFirebaseUser(fbUser?.uid ?: "", fbUser?.email, fbUser?.phoneNumber, fbUser?.displayName, onResult)
                } else {
                    // Try offline login fallback
                    loginOffline(email, pass, onResult)
                }
            }
        } catch(e: Exception) {
             loginOffline(email, pass, onResult)
        }
    }

    fun signUpWithEmail(name: String, email: String, pass: String, phone: String, role: String, onResult: (Boolean, String) -> Unit) {
        try {
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    viewModelScope.launch {
                        val newUser = UserEntity(
                            email = email.trim(),
                            password = pass,
                            name = name.trim(),
                            phone = phone.trim(),
                            role = if (role == "SELLER") "SELLER" else "BUYER",
                            isSellerActive = (role == "SELLER")
                        )
                        val id = dao.insertUser(newUser)
                        if (role == "SELLER") {
                            dao.insertSellerProfile(
                                SellerProfileEntity(
                                    sellerId = id,
                                    shopName = "$name's Kashmiri Handlooms",
                                    location = "Srinagar, Kashmir"
                                )
                            )
                        }
                        val created = dao.getUserById(id)
                        _currentUser.value = created
                        if (created?.role == "SELLER") {
                            _currentScreen.value = "SELLER_MAIN"
                        } else {
                            _currentScreen.value = "BUYER_MAIN"
                        }
                        onResult(true, "Account created successfully")
                    }
                } else {
                    onResult(false, task.exception?.message ?: "Sign Up Failed")
                }
            }
        } catch (e: Exception) {
             onResult(false, "Initialization failed. Check Firebase config.")
        }
    }
"""

# Replace old login/signup methods
import re

pattern = re.compile(r'    fun login\(.*?fun logout\(\) \{', re.DOTALL)
content = pattern.sub(auth_methods + "\n    fun logout() {", content)

with open("app/src/main/java/com/example/ui/MarketViewModel.kt", "w") as f:
    f.write(content)
