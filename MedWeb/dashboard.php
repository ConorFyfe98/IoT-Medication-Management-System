<?php
session_start();
?>

<!DOCTYPE html>
<html>
  <head>
    <meta charset="UTF-8">
    <title>Medication Management System - Dashboard</title>
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.0/css/bootstrap.min.css">
  <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.4.1/jquery.min.js"></script>
  <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.0/js/bootstrap.min.js"></script>
  
  
  <script type="text/javascript" src="https://code.jquery.com/jquery-1.3.2.js" integrity="sha256-IzpdFr7lpkvzvBmr48yBKh4GGUNfAcFj9ih3Okaf9xk="
  crossorigin="anonymous"> </script>

 <script type="text/javascript">

 function loadData() 
   {
	   var rows = document.getElementById('rows').value;
	   var order = document.getElementById('order').value;
	   var status = document.getElementById('status').value;
	   var request = "<?php echo($_SESSION['authorisation']) ?>"; //Pass authorisation level to determine medication data returned
	   console.log("start");
       
      $.ajax({    //create an ajax request to display.php
        type: "POST",
        url: "./API/display.php",  
		data: {numberRows: rows, orderBy: order, medStatus: status, requestFunction: request},
        success: function(response){
				var htmlTable ="";
				var data = JSON.parse(response);
				console.log(data);
			
		if((data[0].error) == undefined){
			//Build table structure
			htmlTable += "<div class='table-responsive'>"
			htmlTable += "<table class='table'>";
			htmlTable += "<thead class='thead-light primaryGreen offWhite'>";
			htmlTable += "<tr>";
			htmlTable += "<th scope='col'>Medication ID</th>";
			htmlTable += "<th scope='col'>User ID</th>";
			htmlTable += "<th scope='l'>Date/Time</th>";
			htmlTable += "<th scope='col'>Pill One</th>";
			htmlTable += "<th scope='col'>Pill Two</th>";
			htmlTable += "<th scope='col'>Pill Three</th>";
			htmlTable += "<th scope='col'>Status</th>";
			htmlTable += "</tr>";
			htmlTable += "</thead>";
			htmlTable += "<tbody>";
           
		   //Display medication data in table
		   for(var i = 0; i <data.length; i++){
                var medicationID = data[i].medicationID;
				var userID = data[i].userID;
                var dateTime =data[i].dateTime;
                var pillOneAmount = data[i].pillOneAmount;
				var pillTwoAmount = data[i].pillTwoAmount;
				var pillThreeAmount = data[i].pillThreeAmount;
                var status = data[i].status;
				
				 htmlTable +=  "<tr>";
				 htmlTable += "<th scope='row'>"+medicationID+"</th>";
				 htmlTable += "<th scope='row'>"+userID+"</th>";
				 htmlTable += "<td>"+dateTime+"</td>";
				 htmlTable += "<td>"+pillOneAmount+"</td>";
				 htmlTable += "<td>"+pillTwoAmount+"</td>";
				 htmlTable += "<td>"+pillThreeAmount+"</td>";
				 htmlTable += "<td>"+status+"</td>";
				 htmlTable += "</tr>";
			}
			
			htmlTable += "</tbody>";
			htmlTable += "</table>";
			htmlTable += "</div>";
		
		}else{
				htmlTable += "<p class'offBlack'>"+data[0].error+"</p>";
		}
			document.getElementById("responsecontainer").innerHTML = htmlTable;
        }

    });
   }
  
 $(document).ready(function() {
	 loadData();

    $("#filter").click(function() {                
		loadData();
});

});
  
</script>
  
    <style type="text/css" media="all">
  
  @import "style.css"
  
</style>
  
  </head>
  <body>
	<div class="jumbotron jumbotron-fluid primaryGreen offWhite text-center">
  <div class="container">
    <h1 class="display-4 primaryGreen offWhite">Medication Management System</h1>
  </div>
	</div>

  
  <div class="container">
  <div class="row">
	<div class="sidePadding">
	<center><img src = "./Images/logo.png" class = "img-responsive" style="height:100px; width=100px;" alt = "Medication Logo"></center>
	
		<?php
	if (isset($_SESSION['loggedIn'])) {
    if ($_SESSION['loggedIn']) {
		
		
        echo "<center><h3 class='offBlack'>Hello <span class='primaryGreenText'>". $_SESSION['firstName']. "</h3>";
		if(($_SESSION['authorisation']) == "Patient"){
		echo "<p class='offBlack'>You can view and analyse your medication progress here or download the Medication Management Application. Additionally, the application features an active day option that sends reminder notifications while you enjoy your day out.</p>";
		}
		
		echo "<a href='API/logout.php'><button href='API/logout.php' type='button' class='btn darkGreenBg offWhite'>Logout</button></a></center>";
    }
	}else { // if user is not logged in display Register and Log in page link
		header('Location: index.php');
	}
    ?>
	
<br></br>
		<?php if(($_SESSION['authorisation']) == "Doctor"){
			  include_once 'includes/adminDashboard.php';
	   		}
			?>
	<h3 class="offBlack" >Search Medication</h3>
	<br>
	<p>Number of rows : <select id="rows" name="rows">
		<option value="10">10</option>
		<option value="30">30</option>
		<option value="50">50</option>
		<option value="All">All</option>
	</select>
	</p>
	
	<p>Order by : <select id="order" name="order">
		<option value="latest">Latest</option>
		<option value="oldest">Oldest</option>
	</select>
	</p>
	
	<p>Status : <select id="status" name="status">
		<option value="All">All</option>
		<option value="Pending">Pending</option>
		<option value="Taken">Taken</option>
		<option value="Missed">Missed</option>
	</select>
	</p>
	

	
	<td> <button type="button" class="btn darkGreenBg offWhite" id="filter">Filter</button></td>
	<br></br>
	<div id="responsecontainer" align="center">

</div>
</div>
</div>
</div>

  </body>
</html>
