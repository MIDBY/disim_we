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


function requiredField(Field1ID, Field2ID){
    const elem1 = document.getElementById(Field1ID);
    const elem2 = document.getElementById(Field2ID);

    if(elem1.value.length > 0 || elem2.value.length > 0) {
        elem1.setAttribute('required',true);
        elem2.setAttribute('required',true);
    } else {
        elem1.removeAttribute('required');
        elem2.removeAttribute('required');
    }
}

function convertTechToUser(event, userId){
  event.preventDefault();
  Swal.fire({
      title: "Are you sure to convert technician to customer?",
      text: "You won't be able to revert this!",
      icon: "warning",
      showCancelButton: true,
      confirmButtonColor: "#3085d6",
      cancelButtonColor: "#d33",
      confirmButtonText: "Yes, convert it!"
    }).then((result) => {
      if (result.isConfirmed) {
        Swal.fire({
          title: "Deleted!",
          text: "Your technician has been promoted to customer.",
          icon: "success"
        }).then(() => {
          $("#converter"+userId).submit();
        });
      }
    });
}

function convertUserToTech(event, userId, userName){
  event.preventDefault();
  Swal.fire({
      title: "Are you sure to convert customer "+userName+" to technician?",
      text: "You won't be able to revert this!",
      icon: "warning",
      showCancelButton: true,
      confirmButtonColor: "#3085d6",
      cancelButtonColor: "#d33",
      confirmButtonText: "Yes, convert it!"
    }).then((result) => {
      if (result.isConfirmed) {
        Swal.fire({
          title: "Congrats!",
          text: "Customer has been promoted to technician.",
          icon: "success"
        }).then(() => {
          document.getElementById(userId).setAttribute("name", "assume");
          $("#converter"+userId).submit();
        });
      }
    });
}

function convertUserToClient(event, userId, userName, userEmail){
  event.preventDefault();
  Swal.fire({
      title: "Are you sure to accept user: "+userName+" with email: "+userEmail+" in our system as customer?",
      text: "You won't be able to revert this!",
      icon: "warning",
      showCancelButton: true,
      confirmButtonColor: "#3085d6",
      cancelButtonColor: "#d33",
      confirmButtonText: "Yes, grant it!"
    }).then((result) => {
      if (result.isConfirmed) {
        Swal.fire({
          title: "Congrats!",
          text: "Your site has a new client.",
          icon: "success"
        }).then(() => {
          document.getElementById(userId).setAttribute("name", "verify");
          document.getElementById(userId).setAttribute("value", "1");
          $("#converter"+userId).submit();
        });
      }
    });
}

function convertClientToUser(event, userId, userName, userEmail){
  event.preventDefault();
  Swal.fire({
      title: "Are you sure to refuse user: "+userName+" with email: "+userEmail+" in our system as customer?",
      text: "You won't be able to revert this!",
      icon: "warning",
      showCancelButton: true,
      confirmButtonColor: "#3085d6",
      cancelButtonColor: "#d33",
      confirmButtonText: "Yes, refuse it!"
    }).then((result) => {
      if (result.isConfirmed) {
        Swal.fire({
          title: "Congrats!",
          text: "Your site has lost a client.",
          icon: "success"
        }).then(() => {
          document.getElementById(userId).setAttribute("name", "verify");
          document.getElementById(userId).setAttribute("value", "0");
          $("#converter"+userId).submit();
        });
      }
    });
}

function errorTechLocked(event){
  event.preventDefault();
  Swal.fire({
      title: "Oops.. Technician is locked",
      text: "You can't fire technician until has requests active! Wait until are closed",
      icon: "error"
    });
}