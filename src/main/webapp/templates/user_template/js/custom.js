function cancelRequest(event, reqId, reqTitle){
  event.preventDefault();
  Swal.fire({
      title: "Are you sure to cancel your request: "+reqTitle+"?",
      html: "You won't be able to revert this!",
      icon: "warning",
      showCancelButton: true,
      confirmButtonColor: "#3085d6",
      cancelButtonColor: "#d33",
      confirmButtonText: "Yes, cancel it!"
    }).then((result) => {
      if (result.isConfirmed) {
        Swal.fire({
          title: "Congrats!",
          text: "Your request has been cancelled.",
          icon: "success"
        }).then(() => {
          $("#cancel"+reqId).submit();
        });
      }
    });
}

function receivedRequest(event, reqId, title){
  event.preventDefault();
  Swal.fire({
      title: "Is arrived product: "+title+"?",
      html: "Did you accept product?",
      input: 'select',
      inputOptions: {
        'ACCETTATO': 'Accettato',
        'RESPINTONONCONFORME': 'Respinto, non conforme',
        'RESPINTONONFUNZIONANTE': 'Respinto, non funzionante'
      },
      inputPlaceholder: 'required',
      showCancelButton: true,
      confirmButtonColor: "#3085d6",
      cancelButtonColor: "#d33",
      confirmButtonText: "Done!",
      inputValidator: function (value) {
        return new Promise(function (resolve, reject) {
          if (value !== '') {
            resolve();
          } else {
            resolve('You must select a option');
          }
        });
      }
    }).then((result) => {
      if (result.isConfirmed) {
        Swal.fire({
          title: "Congrats!",
          text: "Your request has been closed.",
          icon: "success"
        }).then(() => {
          document.getElementById('order'+reqId).setAttribute("value", result.value);
          $("#ship"+reqId).submit();
        });
      }
    });
}

function editProposal(event, proid){
  event.preventDefault();
  Swal.fire({
      title: "Do you accept proposal?",
      html: "You won't be able to revert this!",
      input: "radio",
      inputOptions: {"APPROVATO":"Approvato", "RESPINTO": "Respinto"},
      inputValidator: (value) =>{
        if(!value) {
          return "You must choose one";
        }
      },
      showCancelButton: true,
      confirmButtonColor: "#3085d6",
      cancelButtonColor: "#d33",
    }).then((result) => {
      if (result.isConfirmed) {
        document.getElementById('proposalState'+proid).setAttribute("value", result.value);
        if(result.value == "APPROVATO"){
          Swal.fire({
            title: "Thank you!",
            text: "Your order will be shipped immediatelly!",
            icon: "success",
            confirmButtonColor: "#3085d6"
          }).then(() => {
            document.getElementById('proposalMotivation'+proid).setAttribute("value", result.value);
            $("#edit"+proid).submit();
          });
        } else {
          Swal.fire({
            title: "We're sorry!",
            input: "textarea",
            inputLabel: "Motivation",
            inputPlaceholder: "Type here your motivation...",
            showCancelButton: true,
            confirmButtonColor: "#3085d6",
            cancelButtonColor: "#d33"
          }).then((result) => {
            if (result.isConfirmed){
              document.getElementById('proposalMotivation'+proid).setAttribute("value", result.value);
              $("#edit"+proid).submit();
            }
          });
        }
      }
    });
}

function notShipRequest(event){
  event.preventDefault();
  Swal.fire({
      title: "Your order has not yet been shipped",
      text: "We will contact you when will be shipped",
      icon: "error"
    });
}

function toggle(source) {
  checkboxes = document.getElementsByName('check[]');
  for(var i=0, n=checkboxes.length;i<n;i++) {
    checkboxes[i].checked = source.checked;
  }
}
