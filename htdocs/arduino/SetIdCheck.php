<?php
  $con = mysqli_connect("localhost", "root", "smartpot", "arduino", "3307");
  $id = $_POST["id"];
  // echo $id;
  $query = " UPDATE smartpot SET idCheck = '0' WHERE potCode = ? ";
  $statment = mysqli_prepare($con, $query);
  mysqli_stmt_bind_param($statment, "s", $id);
  mysqli_stmt_execute($statment);
  $response = array();
  $response["success"] = true;

  echo json_encode($response);

?>
