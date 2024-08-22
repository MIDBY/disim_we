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
      text: "You can't fire technician until has requests active! Wait until they closed",
      icon: "error"
    });
}

function acceptRequest(event, reqId, reqTitle, reqChars){
  event.preventDefault();
  var text = '<strong>Title: </string>'+reqTitle+'<br>';
  for(const i of reqChars.split(',')) {
    text += "<strong>"
    const j = i.split(":");
    text += j[0] + ": </strong>" + j[1]
    if(i != reqChars.split(',')[reqChars.split(',').length - 1])
      text +="<br>"
  }
  Swal.fire({
      title: "Are you sure to take request #"+reqId+":",
      html: text,
      icon: "question",
      showCancelButton: true,
      confirmButtonColor: "#3085d6",
      cancelButtonColor: "#d33",
      confirmButtonText: "Yes, accept it!"
    }).then((result) => {
      if (result.isConfirmed) {
        Swal.fire({
          title: "Congrats!",
          text: "This request now is yours! Now you'll be redirected to My Orders.",
          icon: "success"
        }).then(() => {
          $("#take"+reqId).submit();
        });
      }
    });
}

function editProposal(event, propId){
  event.preventDefault();
  Swal.fire({
      title: "Are you sure to edit this proposal?",
      html: "You won't be able to revert this!",
      icon: "warning",
      showCancelButton: true,
      confirmButtonColor: "#3085d6",
      cancelButtonColor: "#d33",
      confirmButtonText: "Yes, edit it!"
    }).then((result) => {
      if (result.isConfirmed) {
        //abilita form 
        document.getElementById("wizard_with_validation").removeAttribute("inert");
        document.getElementById("wizard_with_validation-p-0").removeAttribute("disabled");

        //sposta i dati dallo storico al form
        document.getElementById("productName").value = document.getElementById("productName2").value;
        document.getElementById("producerName").value = document.getElementById("producerName2").value;
        document.getElementById("productDescription").value = document.getElementById("productDescription2").value;
        document.getElementById("productPrice").value = document.getElementById("productPrice2").value;
        document.getElementById("url").value = document.getElementById("url2").value;
        document.getElementById("notes").value = document.getElementById("notes2").value;
      }
    });
}