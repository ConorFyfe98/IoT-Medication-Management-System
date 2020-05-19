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
<title>Medication System - Register</title>
</head>
<body>

  <!--Columns and form-->

<div class="container d-flex align-items-center flex-column h-100 text-white" id="header">
        <div class="form-group">
            <form id="insert_reg" method="POST" action="API/register.php">
				<p class="text-center h4 mb-4 offBlack" style="margin-top:20px;">Registration</p>
				<center><img src = "./Images/logo.png" class = "img-responsive" style="height:100px; width=100px; margin-bottom:10px;" alt = "Medication Logo"></center>
               <input type="text" name="firstName" id="firstName" class="form-control primaryGreenBorder" maxlength="128" placeholder="First Name" required><br>
               <input type="text" name="lastName" id="lastName" class="form-control primaryGreenBorder" maxlength="128" placeholder="Last Name" required><br>
               <input type="email" name="email" id="email" class="form-control primaryGreenBorder" maxlength="128" placeholder="Email" required><br>
               <label for="password" class="offBlack" style="font-size:10pt;">Password must be atleast 8 characters with a letter and number.</label>
               <input type="password" name="password" id="password" class="form-control primaryGreenBorder" maxlength="500" placeholder="Password" required><br>
               <input type="password" name="confirmPassword" id="confirmPassword" class="form-control primaryGreenBorder" maxlength="500" placeholder="Confirm Password" required><br>
               <hr>
			   <center><input name="button" id="submit" class="btn darkGreenBg offWhite btn-block" type="submit" value="Sign up"></center>
           </form>
		   		   	   <?php 
					   //Display message if session not empty
					   if(!empty($_SESSION['registerErrorMessage'])){
		   echo "<br><center><div class='alert alert-danger' role='alert'>". $_SESSION['registerErrorMessage']. "</div></center>";
	   }		   
	   unset($_SESSION['registerErrorMessage']);?>
</div>
<center><span class="offBlack">Already a member? <span><span class="darkGreen"><a href="./index.php">Sign-in here</a></span></center>
</div>
</body>
</html>