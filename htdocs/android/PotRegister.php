<?php
  $con = mysqli_connect("localhost", "root", "smartpot", "arduino", "3307");

  $potName = $_POST["potName"];
  $userID = $_POST["userID"];
  $potCode = $_POST["potCode"];

  mysqli_query($con, "UPDATE smartpot SET potName = '$potName', userID = '$userID', startday = now() WHERE potCode = '$potCode' ");

  $response = array();
  $response["success"] = true;

  echo json_encode($response);
?>
