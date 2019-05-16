<?php
  $con = mysqli_connect("localhost", "root", "smartpot", "arduino", "3307");

  $potCode = $_POST["id"];

  $query = "UPDATE smartpot SET auto = '1', manual = '0', update_time = now() WHERE potCode = ? ";

  $statment = mysqli_prepare($con, $query);
  mysqli_stmt_bind_param($statment, "s", $potCode);
  mysqli_stmt_execute($statment);
  $response = array();
  $response["success"] = true;

  echo json_encode($response);
?>
