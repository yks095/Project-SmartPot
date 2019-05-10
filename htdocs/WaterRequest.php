<?php
  $con = mysqli_connect("localhost", "root", "didrltjr3", "android", "3306");

  $auto = $_POST["auto"];
  $potCode = $_POST["potCode"];


  if (mysqli_connect_errno())   {
    echo "Failed to connect to MySQL: " . mysqli_connect_error();
    }

  mysqli_query($con,"UPDATE SMARTPOT SET auto='$auto' WHERE potCode='$potCode' ");

   // mysqli_stmt_bind_param($statment, "s", $potname);
     // mysqli_stmt_execute($statment);

     $response = array();
     $response["success"] = true;

     echo json_encode($response);
  ?>
