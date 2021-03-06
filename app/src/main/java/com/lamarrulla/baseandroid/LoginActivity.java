package com.lamarrulla.baseandroid;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
/*import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;*/
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.lamarrulla.baseandroid.implement.Acceso;
import com.lamarrulla.baseandroid.interfaces.IAcceso;
import com.lamarrulla.baseandroid.utils.SlideToUnlock;
import com.lamarrulla.baseandroid.utils.Utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Permission;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor>, View.OnClickListener, SlideToUnlock.OnSlideToUnlockEventListener {

    Context context = this;
    Utils utils = new Utils();
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;

    IAcceso iAcceso = new Acceso();
    CallbackManager callbackManager = CallbackManager.Factory.create();
    private static final String TAG = "LoginActivity";
    private static final int RC_SIGN_IN = 9000;
    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_PERMISSIONS = 0;
    //private static final int MY_PERMISSIONS_REQUEST_PHONE = 0;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private TextView txtRestablece;
    private FloatingActionButton fabTelephone;

    private int funcion;
    private int tipoAcceso;

    private SlideToUnlock slideToUnlockView2;

    View focusView = null;

    AlertDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        utils.setContext(context);
        fabTelephone = findViewById(R.id.fabTelephone);
        fabTelephone.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
        mAuth.setLanguageCode("Sp");
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(new Scope(Scopes.DRIVE_APPFOLDER))
                .requestServerAuthCode("129048150139-p325stbnhjgm18ck47js8r0qskie3fki.apps.googleusercontent.com")
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        //Valida Hash // Si tienes problemas que no cuadre tu hash puedes descomentar esta funcion
        //validaHash();

        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        mEmailView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_NEXT) {
                    if(validaEmail(textView.getText().toString())){
                        mEmailView.requestFocus();
                    }
                }
                return false;
            }
        });
        populateAutoComplete();

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    if (utils.isConnectAvailable(context)) {
                        funcion = 1;
                        attemptLogin();
                        return true;
                    } else {
                        Toast.makeText(context, getString(R.string.noConexionInternet), Toast.LENGTH_SHORT).show();
                        return false;
                    }

                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(this);

        /*mLoginFormView = findViewById(R.id.login_form);*/
        mLoginFormView = findViewById(R.id.email_login_form);
        mProgressView = findViewById(R.id.login_progress);
        txtRestablece = findViewById(R.id.txtRestablece);

        /*  Facebook */

        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("email", "public_profile");
        //Callback registration
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
                utils.guardaShared((Activity) context, R.string.TipoAcceso, "3");
            }

            @Override
            public void onCancel() {
                Toast.makeText(LoginActivity.this, getString(R.string.operacionCancelada), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "Error al logear con facebook: " + error);
                switch (error.getMessage()) {
                    case "CONNECTION_FAILURE: CONNECTION_FAILURE":
                        Toast.makeText(LoginActivity.this, getString(R.string.noConexionInternet), Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(LoginActivity.this, getString(R.string.errroSesionFacebook), Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
        /*  End Facebook */

        /*  Boton Registrar */
        /*
        Button mRegistrarButton = (Button) findViewById(R.id.email_alta_button);
        mRegistrarButton.setOnClickListener(this);
        */
        /*  End Boton Registrar*/
        /*  Boton Gmail */
        //findViewById(R.id.sign_in_button).setOnClickListener(this);
        /*  End Boton Gmail */

        slideToUnlockView2 = (SlideToUnlock) findViewById(R.id.slideToUnlock2);
        slideToUnlockView2.setExternalListener(this);

        txtRestablece.setOnClickListener(this);
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);
        showProgress(true);
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            //FirebaseUser user = mAuth.getCurrentUser();
                            //utils.guardaShared((Activity) context, R.string.Token, user.toString());
                            utils.OpenMain(context);
                            showProgress(false);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            showProgress(false);
                        }
                    }
                });
    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_PERMISSIONS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_PERMISSIONS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSIONS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED && permissions[0].equals("android.permission.READ_CONTACTS")) {
                populateAutoComplete();
            }else if(grantResults.length>0 && grantResults[1] == PackageManager.PERMISSION_GRANTED && permissions[1].equals("android.permission.READ_PHONE_NUMBERS")){
                Toast.makeText(context, "Se otorgaron los permisos", Toast.LENGTH_SHORT).show();
                loginPhone();
            }else{
                Toast.makeText(context, "El logeo telefónico necesita acceder a tu número telefónico, por favor autoriza los permisos", Toast.LENGTH_SHORT).show();
            }
        }
        /*else if(requestCode == MY_PERMISSIONS_REQUEST_PHONE){
            if(grantResults.length>0 && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(context, "Se otorgaron los permisos", Toast.LENGTH_SHORT).show();
                loginPhone();
            }else{
                Toast.makeText(context, "El logeo telefónico necesita acceder a tu número telefónico, por favor autoriza los permisos", Toast.LENGTH_SHORT).show();
            }
        }*/
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        /*
            1.- Login
            2.- Registro
        */
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        /*boolean cancel = false;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password) || !utils.isPasswordValid(password)) {
            if (TextUtils.isEmpty(password)) {
                mPasswordView.setError(getString(R.string.passworParaContinuar));
            } else {
                mPasswordView.setError(getString(R.string.error_invalid_password));
            }
            focusView = mPasswordView;
            cancel = true;
        }*/

        boolean cancel = validaEmail(email);

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            utils.showProgress(mLoginFormView, mProgressView, context);
            iAcceso.setContext(context);
            tipoAcceso = getResources().getInteger(R.integer.AccesoUsuarioConstasenna);
            if (tipoAcceso == 1) {
                mAuthTask = new UserLoginTask(email, password);
                mAuthTask.execute((Void) null);
            } else {
                iAcceso.setUsername(email);
                iAcceso.setPassword(password);
                switch (funcion) {
                    case 1:
                        /*  autentica usuario por firebase  */
                        iAcceso.autenticaUsuarioFirebase();
                        break;
                    case 2:
                        /*  alta usurio por firebase    */
                        iAcceso.altaUsuarioFirebase();
                        break;
                }
            }
        }
    }

    private boolean validaEmail(String email){
        // Check for a valid email address.
        boolean vEmail = false;
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            vEmail = true;
        } else if (!utils.isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            vEmail = true;
        }
        return vEmail;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.email_sign_in_button:
                if (utils.isConnectAvailable(context)) {
                    funcion = 1;
                    attemptLogin();
                } else {
                    Toast.makeText(context, getString(R.string.noConexionInternet), Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.txtRestablece:
                //Toast.makeText(context, "Click restablece", Toast.LENGTH_SHORT).show();
                String emailAddress = mEmailView.getText().toString();

                if (utils.isEmailValid(emailAddress)) {
                    mAuth.sendPasswordResetEmail(emailAddress)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        //Log.d(TAG, "Email sent.");
                                        Toast.makeText(context, "La contraseña se reestablecio correctamente, valida tu correo.", Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(context, "Ocurrio un error al restablecer tu contraseña.", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                } else {
                    mEmailView.setError(getString(R.string.error_no_email));
                    focusView = mEmailView;
                    focusView.requestFocus();
                }
                break;
            case R.id.fabTelephone:
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.READ_SMS,
                                    Manifest.permission.READ_PHONE_NUMBERS,
                                    Manifest.permission.READ_PHONE_STATE},
                            REQUEST_PERMISSIONS);
                    return;
                }else{
                    loginPhone();
                }


                break;
            /*case R.id.sign_in_button:
                signIn();
                break;*/
                default:break;
        }
    }

    private void loginPhone(){
        //String phoneNumber = "+" + utils.numberToPhone(utils.getPhoneNumber());
        //phoneNumber = "+52 987 654 3210";
        //phoneNumber = "+52 556 800 9630";
        String phoneNumber = "+52 551 398 0540";
        Log.d(TAG, "phoneNumber: " + phoneNumber);

        Toast.makeText(context, getString(R.string.autenticacionTelefonica), Toast.LENGTH_SHORT).show();

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential credential) {
                        // This callback will be invoked in two situations:
                        // 1 - Instant verification. In some cases the phone number can be instantly
                        //     verified without needing to send or enter a verification code.
                        // 2 - Auto-retrieval. On some devices Google Play services can automatically
                        //     detect the incoming verification SMS and perform verification without
                        //     user action.
                        Log.d(TAG, "onVerificationCompleted:" + credential);
                        signInWithPhoneAuthCredential(credential);
                        //Toast.makeText(context, "Verificaion Completa", Toast.LENGTH_SHORT).show();

                        //signInWithPhoneAuthCredential(credential);
                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        // This callback is invoked in an invalid request for verification is made,
                        // for instance if the the phone number format is not valid.
                        Log.w(TAG, "onVerificationFailed", e);

                        if (e instanceof FirebaseAuthInvalidCredentialsException) {
                            // Invalid request
                            // ...
                            switch (((FirebaseAuthInvalidCredentialsException)   e).getErrorCode()){
                                case "ERROR_APP_NOT_AUTHORIZED":
                                    Toast.makeText(context, "This app is not authorized to use Firebase Authentication. Please verifythat the correct package name and SHA-1 are configured in the Firebase Console. [ App validation failed. Is app running on a physical device? ]", Toast.LENGTH_SHORT).show();
                                    break;
                                    default:
                                        Toast.makeText(context, "Ocurrio un error al validar las credenciales", Toast.LENGTH_SHORT).show();
                                        break;
                            }
                        } else if (e instanceof FirebaseTooManyRequestsException) {
                            // The SMS quota for the project has been exceeded
                            // ...
                            Toast.makeText(context, "La cuota de SMS se ha excedido", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        // Show a message and update the UI
                        // ...
                    }

                    @Override
                    public void onCodeSent(String verificationId,
                                           PhoneAuthProvider.ForceResendingToken token) {
                        // The SMS verification code has been sent to the provided phone number, we
                        // now need to ask the user to enter the code and then construct a credential
                        // by combining the code with a verification ID.
                        Log.d(TAG, "onCodeSent:" + verificationId);

                        createDialog(verificationId);

                        // Save verification ID and resending token so we can use them later
                        //mVerificationId = verificationId;
                        //mResendToken = token;

                        // ...
                    }
                });
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onSlideToUnlockCanceled() {
        Toast.makeText(this, getString(R.string.deslizaAlta), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onSlideToUnlockDone() {
        if(utils.isConnectAvailable(context)){
            funcion = 2;
            attemptLogin();
        }else{
            Toast.makeText(context, getString(R.string.noConexionInternet), Toast.LENGTH_SHORT).show();
        }
    }

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                // Valida Autenticacion
                iAcceso.setUsername(mEmail);
                iAcceso.setPassword(mPassword);
                switch (funcion){
                        case 1:
                            //autentica usuario por servidor
                            iAcceso.autenticaUsuarioServer();
                            break;
                        case 2:
                            //Alta usuario por servidor
                            iAcceso.altaUsarioServer();
                            cancel(true);
                            break;
                        default:
                            iAcceso.autenticaUsuarioServer();
                            break;
                }


                //Thread.sleep(2000);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }

            // TODO: register the new account here.
            return iAcceso.getEsCorrecto();
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if(funcion==1){
                if (success) {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    mPasswordView.setError(getString(R.string.error_incorrect_password));
                    mPasswordView.requestFocus();
                }
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        //Toast.makeText(LoginActivity.this, "ActivityResult", Toast.LENGTH_SHORT).show();
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        Log.d(TAG, data.toString() + requestCode);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            String authCode = account.getServerAuthCode();
            firebaseAuthWithGoogle(account);
            Log.d(TAG, authCode);
            // Signed in successfully, show authenticated UI.
            //updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            //updateUI(null);
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            //FirebaseUser user = mAuth.getCurrentUser();
                            //utils.guardaShared((Activity) context, R.string.Token, user.toString());
                            utils.OpenMain(context);
                            showProgress(false);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Snackbar.make(findViewById(R.id.email_login_form), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            showProgress(false);
                            //updateUI(null);
                        }
                    }
                });
    }

    public void OpenMain(Context context){
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        showProgress(false);
    }

    private void validaHash(){
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.lamarrulla.baseandroid",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            System.out.println(e.getMessage());
        }
    }

    public void createDialog(final String verificationId){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);
        View mViewAgregar = getLayoutInflater().inflate(R.layout.fragment_autentica_codigo, null);
        mBuilder.setView(mViewAgregar);
        dialog = mBuilder.create();
        final EditText eT0 = mViewAgregar.findViewById(R.id.eT0);
        final EditText eT1 = mViewAgregar.findViewById(R.id.eT1);
        final EditText eT2 = mViewAgregar.findViewById(R.id.eT2);
        final EditText eT3 = mViewAgregar.findViewById(R.id.eT3);
        final EditText eT4 = mViewAgregar.findViewById(R.id.eT4);
        final EditText eT5 = mViewAgregar.findViewById(R.id.eT5);
        final Button btnEnviar = mViewAgregar.findViewById(R.id.btnEnviar);
        final ImageView btnCerrar = mViewAgregar.findViewById(R.id.btnCerrarAC);

        eT0.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                eT1.requestFocus();
                return false;
            }
        });

        eT1.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                eT2.requestFocus();
                return false;
            }
        });

        eT2.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                eT3.requestFocus();
                return false;
            }
        });

        eT3.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                eT4.requestFocus();
                return false;
            }
        });

        eT4.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                eT5.requestFocus();
                return false;
            }
        });

        eT5.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                String eT0T = eT0.getText().toString();
                String eT1T = eT1.getText().toString();
                String eT2T = eT2.getText().toString();
                String eT3T = eT3.getText().toString();
                String eT4T = eT4.getText().toString();
                String eT5T = KeyEvent.keyCodeToString(keyCode).replaceFirst("KEYCODE_", "");
                String TotalET = eT0T + eT1T + eT2T + eT3T + eT4T + eT5T;
                if(eT0T.length() == 0 || eT1T.length() == 0 | eT2T.length() == 0 || eT3T.length() == 0 || eT4T.length() == 0 || eT5T.length() == 0){
                    Toast.makeText(context, "Todos los campos tienen que contener por lo menos un caracter", Toast.LENGTH_SHORT).show();
                }else{
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId,TotalET) ;
                    //Toast.makeText(context, "Credencial: " + credential, Toast.LENGTH_LONG).show();
                    signInWithPhoneAuthCredential(credential);
                    dialog.hide();
                }
                return false;
            }
        });

        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String eT0T = eT0.getText().toString();
                String eT1T = eT1.getText().toString();
                String eT2T = eT2.getText().toString();
                String eT3T = eT3.getText().toString();
                String eT4T = eT4.getText().toString();
                String eT5T = eT5.getText().toString();
                String TotalET = eT0T + eT1T + eT2T + eT3T + eT4T + eT5T;
                if(eT0T.length() == 0 || eT1T.length() == 0 | eT2T.length() == 0 || eT3T.length() == 0 || eT4T.length() == 0 || eT5T.length() == 0){
                    Toast.makeText(context, "Todos los campos tienen que contener por lo menos un caracter", Toast.LENGTH_SHORT).show();
                }else{
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId,TotalET) ;
                    //Toast.makeText(context, "Credencial: " + credential, Toast.LENGTH_LONG).show();
                    signInWithPhoneAuthCredential(credential);
                    dialog.hide();
                }
            }
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        btnCerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.hide();
            }
        });
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            //Toast.makeText(context, "signInWithCredential:success", Toast.LENGTH_SHORT).show();
                            //FirebaseUser user = task.getResult().getUser();
                            // ...
                            utils.OpenMain(context);
                            showProgress(false);
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                if(task.getException().getMessage().equals(getString(R.string.CodeVerficacionInvalidoIng))){
                                    Toast.makeText(context, getString(R.string.CodeVerificacionInvalidoEsp), Toast.LENGTH_LONG).show();
                                }else{
                                    Toast.makeText(context, "Ocurrio un error en la autenticación del código", Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    }
                });
    }

}