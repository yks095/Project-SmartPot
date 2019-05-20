<?php
  $con = mysqli_connect("localhost", "root", "smartpot", "arduino", "3307");

  $userID = $_POST["userID"];
  $userPassword = $_POST["userPassword"];


  if (mysqli_connect_errno())   {
    echo "Failed to connect to MySQL: " . mysqli_connect_error();
  }

  mysqli_query($con,"UPDATE USER SET userPassword='$userPassword' WHERE userID='$userID' ");

  $response = array();
  $response["success"] = true;

  echo json_encode($response);
?>
