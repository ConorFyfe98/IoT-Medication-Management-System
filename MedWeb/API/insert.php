<?php
session_start();
include_once 'connection.php';
//Run once button is pressed.
if(isset($_POST['insertButton'])){

//Validate input
if(empty($_POST["userID"]) || empty($_POST["inputDate"]) || empty($_POST["inputTime"]) || is_null($_POST["pillOneAmount"]) || is_null($_POST["pillTwoAmount"]) || is_null($_POST["pillThreeAmount"])){
	$_SESSION['insertError'] = "Please fill out all fields.";
	header("Location: ../dashboard.php");
	}else{

//Initiate variables
$userID = filter_input(INPUT_POST, 'userID', FILTER_SANITIZE_STRING);
$inputDate = date('Y-m-d', strtotime($_POST['inputDate']));
$inputTime = ($_POST["inputTime"]);
$pillOneAmount = filter_input(INPUT_POST, 'pillOneAmount', FILTER_SANITIZE_STRING);
$pillTwoAmount = filter_input(INPUT_POST, 'pillTwoAmount', FILTER_SANITIZE_STRING);
$pillThreeAmount = filter_input(INPUT_POST, 'pillThreeAmount', FILTER_SANITIZE_STRING);

//Join date and time
$combinedDT = date('Y-m-d H:i:s', strtotime("$inputDate $inputTime"));

//Build sql query
$query = $conn->prepare("
            INSERT INTO medicationData(dateTime, userID, pillOneAmount, pillTwoAmount, pillThreeAmount, status)
            VALUES (:dateTime, $userID, :pillOneAmount, :pillTwoAmount, :pillThreeAmount, 'Pending')");

	$success = $query->execute([
    'dateTime' => $combinedDT,
    'pillOneAmount' => $pillOneAmount,
    'pillTwoAmount' => $pillTwoAmount,
    'pillThreeAmount' => $pillThreeAmount
        ]);

$count = $query->rowCount();

//If row count over 1 success else error
if ($count > 0) {
	$_SESSION['insertSuccess'] = "Medication Added.";
    header("Location: ../dashboard.php");
} else {
	$_SESSION['insertError'] = "An unknown error occured.";
	header("Location: ../dashboard.php");
}

}
}
?>