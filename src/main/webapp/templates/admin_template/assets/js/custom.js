//Custom functions for admin template of webshop project

function showPassword(targetID) {
    var x = document.getElementById(targetID);
  
    if (x.type === "password") {
      x.type = "text";
    } else {
      x.type = "password";
    }
  }

function showPassword(clickID, targetID) {    
    const passwordField = document.getElementById(targetID);
    const togglePassword = document.getElementById(clickID);
    if(passwordField.type === "password") {
        passwordField.type = "text";
        togglePassword.classList.remove("zmdi-lock");
        togglePassword.classList.add("zmdi-lock-open");
    } else {
        passwordField.type = "password";
        togglePassword.classList.remove("zmdi-lock-open");
        togglePassword.classList.add("zmdi-lock");
    }
}

function setAutocomplete() {
    const login = document.getElementById("loginForm");
    if(login.getAttribute("autocomplete") === "on") {
        $('#loginForm').attr('autocomplete', 'off');
    } else {
        $('#loginForm').attr('autocomplete', 'on');
    }
}