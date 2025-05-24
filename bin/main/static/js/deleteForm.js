
  window.addEventListener("load", function() {
            console.log('window loaded');
      let form = document.getElementById('form2');
      form.addEventListener("submit", function(event) {
       console.log('submit pushed');
             if (confirm("Are you sure you want this data deleted?")) {
                alert("Deleting data.");
                 console.log('True: will delete');
             } else {
                alert("Data preserved.");
                 console.log("False: won't delete");
                event.preventDefault();
             }
      });
   });
