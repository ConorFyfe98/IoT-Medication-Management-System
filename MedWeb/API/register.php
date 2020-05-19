<?php
session_start();
include 'connection.php';

if(isset($_POST['button'])){
 
  $firstName = filter_input(INPUT_POST, 'firstName', FILTER_SANITIZE_STRING);
  $lastName = filter_input(INPUT_POST, 'lastName', FILTER_SANITIZE_STRING);
  $email = filter_input(INPUT_POST, 'email', FILTER_SANITIZE_EMAIL);
  $password = filter_input(INPUT_POST, 'password', FILTER_SANITIZE_STRING);
  $connfirmPassword = filter_input(INPUT_POST, 'confirmPassword', FILTER_SANITIZE_STRING);

//Check no fields are empty, if empty display error message.
    if(empty($_POST["firstName"]) || empty($_POST["lastName"]) || empty($_POST["email"]) ||  empty($_POST["password"]) ||  empty($_POST["confirmPassword"])){ 
	$registerErrorMessage = "Please ensure all fields are filled in.";
	returnError($registerErrorMessage);
	}else{

     //validate input
     if (strlen($firstName) > 30){ 
       $registerErrorMessage = "First name must contain less than 30 characters.";
	   returnError($registerErrorMessage);
     }else{
       
      if (!preg_match("/^([a-zA-Z']+)$/",$firstName)) {
        $registerErrorMessage =  "First name must only contain letters."; 
		returnError($registerErrorMessage);
      }else{
       
       if (!preg_match("/^([a-zA-Z']+)$/",$lastName)) {
        $registerErrorMessage =  "Last name must only contain letters.";
		returnError($registerErrorMessage);		
      }else{
   
       if (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
        $registerErrorMessage =  "Invalid email format";
		returnError($registerErrorMessage);
      }else{

// Ensure password is strong
       if (strlen($password) < 8) {
         $registerErrorMessage =  "Password is too short.";
		 returnError($registerErrorMessage);
       }else{
        
        if (!preg_match("#[0-9]+#", $password)) {
          $registerErrorMessage =  "Password must include at least one number.";
		  returnError($registerErrorMessage);
        }else{
          
         if (!preg_match("#[a-zA-Z]+#", $password)) {
          $registerErrorMessage =  "Password must include at least one letter.";
		  returnError($registerErrorMessage);
        }else{
		
			//Ensure that password and conformation password match
          if( $_POST['confirmPassword'] == ($_POST['password'])){

			$password= password_hash($password, PASSWORD_DEFAULT);
			
			//attempt registration
		 $query = $conn->prepare("
  INSERT INTO medicationUsers(email, password, firstName, lastName, authorisation)
  VALUES (:email, :password, :firstName, :lastName, 'Patient')");
 
// assign parameters
 $success = $query->execute([
  'email' => $email,
  'password' => $password,
  'firstName' => $firstName,
  'lastName' => $lastName
]);

 $count = $query->rowCount();
//if row count is greater than 0 user is now registered
 if($count > 0) {

	if(isset($_POST['app'])){
			$response["status"] = 0;
			echo json_encode($response);
			
		}else{
		header("Location: ../index.php");
		}
} else {
  $registerErrorMessage =  "Email already exists.";
	returnError($registerErrorMessage);
}
          
		  }
          else
          { 
            $registerErrorMessage =  "Passwords do not match.";
			returnError($registerErrorMessage);
          }
        }
      }
    }
  }
}
}
}
}
}

//return error message to display
function returnError($registerErrorMessage){

	if(isset($_POST['app'])){
			$response["status"] = 1;
			$response["message"] = $registerErrorMessage;
			echo json_encode($response);
			
		}else{
$_SESSION['registerErrorMessage'] = $registerErrorMessage;
echo $registerErrorMessage;
header('Location: ../register.php');
		}
}

?>