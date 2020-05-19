       
	   <?php
	   
       session_start();
	   include 'connection.php';
	   
       if(isset($_POST['button'])){
        $email = filter_input(INPUT_POST,'email', FILTER_SANITIZE_STRING);
        $password = filter_input (INPUT_POST,'password', FILTER_SANITIZE_STRING);


		//Check no fields are empty, if empty display error message.
          if(empty($_POST["email"]) || empty($_POST["password"])){ echo "Please ensure all fields are filled in."; }else{
			
			//Sql to retrieve email and password
            $sql = "SELECT * FROM medicationUsers WHERE email = :email"; 
            $stmt = $conn->prepare($sql);
            $success = $stmt->execute(['email'=> $email]);	
            
			
			if($success && $stmt->rowCount() > 0){
			$userData = $stmt->fetch(PDO::FETCH_OBJ);
			
			$hashedPassword = $userData->password;

			//Check if password matches password in database
			if(password_verify($password, $hashedPassword)){
             $userID = $userData->userID;
			 $firstName = $userData->firstName;
			 $authorisation = $userData->authorisation;
			
			if(isset($_POST['app'])){
				$response["status"] = 0;
				$response["firstName"] = $firstName;
				$response['authorisation'] = $authorisation;
				$response['userID'] = $userID;
				
				echo json_encode($response);
				
			}else{
			$_SESSION['firstName'] = $firstName;
			$_SESSION['userID'] = $userID;
			$_SESSION['authorisation'] = $authorisation;
			$_SESSION['loggedIn'] = true;
			header("Location: ../dashboard.php");
			}
			}else{
				errorMessage();
			}
       }
       else
       {
			errorMessage();
      }
    }
  }
  
  //Display error message informin user details are incorrect
  function errorMessage(){
	  		if(isset($_POST['app'])){
			$response["status"] = 1;
			$response["message"] = "Email and password combination is incorrect.";
			
			echo json_encode($response);
			
		}else{
		$_SESSION['loggedIn'] = false;
		$_SESSION['loginErrorMessage'] = "Email and password combination is incorrect.";
		header('Location: ../index.php');
		}
	  
  }

$cookie_name = "email";
$cookie_value = $un;
setcookie($cookie_name, $cookie_value, time() + (86400 * 30), "/"); // 86400 = 1 day
?>