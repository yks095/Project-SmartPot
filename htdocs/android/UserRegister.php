<?php include "./ServerName.php";
  $con = mysqli_connect($server_name, $user_name, $user_password, $db_name, $port);
  $userID = $_POST["userID"];
  $userPassword = $_POST["userPassword"];
  $userGender = $_POST["userGender"];
  $userEmail = $_POST["userEmail"];

  $statment = mysqli_prepare($con, "INSERT INTO USER VALUES (?, ?, ?, ?)");
  mysqli_stmt_bind_param($statment, "ssss", $userID, $userPassword, $userGender, $userEmail);
  mysqli_stmt_execute($statment);

  $response = array();
  $response["success"] = true;

  echo json_encode($response);
?>
