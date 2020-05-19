<?php
session_start();
?>

<!DOCTYPE html>
<html lang="en">
<head>
  <!-- Required meta tags -->
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">

  <!-- Bootstrap CSS -->
  
  <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/css/bootstrap.min.css" integrity="sha384-Vkoo8x4CGsO3+Hhxv8T/Q5PaXtkKtu6ug5TOeNV6gBiFeWPGFN9MuhOf23Q9Ifjh" crossorigin="anonymous">
  
  <script src="https://code.jquery.com/jquery-3.4.1.slim.min.js" integrity="sha384-J6qa4849blE2+poT4WnyKhv5vZF5SrPo0iEjwBvKU7imGFAV0wwj1yYfoRSJoZ+n" crossorigin="anonymous"></script>
<script src="https://cdn.jsdelivr.net/npm/popper.js@1.16.0/dist/umd/popper.min.js" integrity="sha384-Q6E9RHvbIyZFJoft+2mJbHaEWldlvI9IOYy5n3zV9zzTtmI3UksdQRVvoxMfooAo" crossorigin="anonymous"></script>
<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/js/bootstrap.min.js" integrity="sha384-wfSDF2E50Y2D1uUdj0O3uMBJnjuUD4Ih7YwaYd1iqfktj0Uod8GCExl3Og8ifwB6" crossorigin="anonymous"></script>
  
  
	<script>
	//Show/Hide Password
	function passwordVisibility() {
    var password = document.getElementById("password");
    if (password.type === "password") {
        password.type = "text";
    } else {
        password.type = "password";
    }
} 
	
	</script>
  <style type="text/css" media="all">
  
  @import "style.css"
  
</style>
<title>Medication System - Index</title>
</head>
<body>

  <!--Columns and form-->
<div class="container d-flex align-items-center flex-column justify-content-center h-100 text-white" id="header">
    <h1 class="display-4 offBlack">Welcome</h1>
	<img src = "./Images/logo.png" class = "img-responsive" style="height:100px; width=100px;" alt = "Medication Logo">
        <div class="form-group">
        <form id="attempt_login" method="POST" name="login" action="API/login.php">
         <p class="text-center h4 mb-4 offBlack">Medication System Management</p>
         <input type="text" class="form-control primaryGreenBorder" name="email" id="email" maxlength="255" placeholder="Email" required><br>
        
         <input type="password" class="form-control primaryGreenBorder" name="password" id="password" maxlength="500" placeholder="Password" required><br>
		
		 <label for="checkbox" class="offBlack">Show password: </label>
		 <input type="checkbox" onclick="passwordVisibility()" name ="checkbox" id ="checkbox">
		 <hr>
         <center><button id="button" name="button" class="btn darkGreenBg offWhite btn-block" id="submit" type="submit" value="Log in">Sign in</button></center>
       </form>
		   <?php 
		   //If Session is not empty display message
		   if(!empty($_SESSION['loginErrorMessage'])){
		   echo "<br><center><div class='alert alert-danger' role='alert'>". $_SESSION['loginErrorMessage']. "</div></center>";
	   }		   
	   unset($_SESSION['loginErrorMessage']);?>
</div>
<center><span class="offBlack">Not a member? <span><span class="darkGreen"><a href="./register.php">Register here</a></span></center>
</div>

</body>
</html>