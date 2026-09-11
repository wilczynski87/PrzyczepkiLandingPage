package com.example.przyczepki_landingpage

import kotlin.js.ExperimentalWasmJsInterop

private var googleAuthCallback: ((Result<String>) -> Unit)? = null

@OptIn(ExperimentalWasmJsInterop::class)
@JsFun(
    """
    (onOk, onErr) => {
      window.__przyczepkiGoogleSuccess = onOk;
      window.__przyczepkiGoogleError = onErr;
    }
    """
)
private external fun registerGoogleAuthJsCallbacks(onOk: (String) -> Unit, onErr: (String) -> Unit)

@OptIn(ExperimentalWasmJsInterop::class)
@JsFun(
    """
    (clientId) => {
      const fail = (msg) => {
        if (window.__przyczepkiGoogleError) window.__przyczepkiGoogleError(msg);
      };
      const ok = (token) => {
        if (window.__przyczepkiGoogleSuccess) window.__przyczepkiGoogleSuccess(token);
      };
      const google = window.google;
      if (!google || !google.accounts || !google.accounts.id) {
        const nonce = Math.random().toString(36).slice(2);
        const redirect = window.location.origin + window.location.pathname;
        const url = 'https://accounts.google.com/o/oauth2/v2/auth'
          + '?client_id=' + encodeURIComponent(clientId)
          + '&redirect_uri=' + encodeURIComponent(redirect)
          + '&response_type=id_token'
          + '&scope=' + encodeURIComponent('openid email profile')
          + '&nonce=' + encodeURIComponent(nonce)
          + '&prompt=select_account';
        window.location.assign(url);
        return;
      }
      google.accounts.id.initialize({
        client_id: clientId,
        callback: (response) => {
          if (response && response.credential) ok(response.credential);
          else fail('Nie udało się uzyskać tokenu Google.');
        }
      });
      google.accounts.id.prompt((notification) => {
        if (notification.isNotDisplayed() || notification.isSkippedMoment()) {
          const id = 'google-signin-fallback';
          let el = document.getElementById(id);
          if (!el) {
            el = document.createElement('div');
            el.id = id;
            el.style.position = 'fixed';
            el.style.left = '-9999px';
            document.body.appendChild(el);
          } else {
            el.innerHTML = '';
          }
          google.accounts.id.renderButton(el, { theme: 'outline', size: 'large', type: 'standard' });
          const btn = el.querySelector('[role=button]');
          if (btn) btn.click();
          else fail('Nie udało się otworzyć logowania Google. Spróbuj ponownie.');
        }
      });
    }
    """
)
private external fun startGoogleSignInJs(clientId: String)

@OptIn(ExperimentalWasmJsInterop::class)
@JsFun("() => window.location.hash || ''")
private external fun locationHashJs(): String

@OptIn(ExperimentalWasmJsInterop::class)
@JsFun(
    """
    (hash) => {
      const path = window.location.pathname + window.location.search + (hash || '');
      window.history.replaceState(null, '', path);
    }
    """
)
private external fun replaceLocationHashJs(hash: String)

actual fun requestGoogleIdToken(clientId: String, onResult: (Result<String>) -> Unit) {
    if (clientId.isBlank()) {
        onResult(Result.failure(Exception("Logowanie Google nie jest skonfigurowane")))
        return
    }
    googleAuthCallback = onResult
    registerGoogleAuthJsCallbacks(
        onOk = { token ->
            googleAuthCallback?.invoke(Result.success(token))
            googleAuthCallback = null
        },
        onErr = { message ->
            googleAuthCallback?.invoke(Result.failure(Exception(message)))
            googleAuthCallback = null
        },
    )
    startGoogleSignInJs(clientId)
}

actual fun getLocationHash(): String = locationHashJs()

actual fun replaceLocationHash(hash: String) {
    replaceLocationHashJs(hash)
}
