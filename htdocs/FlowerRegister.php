<?php
      $con = mysqli_connect("localhost", "root", "smartpot", "arduino", "3307");

      $flower = $_POST["flower"];
      $userID = $_POST["userID"];

      if (mysqli_connect_errno()) {
          echo "Failed to connect to MySQL: " . mysqli_connect_error();
      }


        $query = "SELECT * FROM FLOWER WHERE flower = '$flower'  ";
        $idx = mysqli_query($con, $query);
        $row=mysqli_fetch_array($idx);
        mysqli_query($con,"UPDATE SMARTPOT SET flower = $row[0] WHERE userID='$userID' ");


      //mysqli_query($con,"UPDATE FLOWER SET flower1 = '$flower1', flower2 = '$flower2', flower3 = '$flower3', flower4 = '$flower4' WHERE potName='aaa' AND
        //(flower1 is null OR flower2 is null OR flower3 is null OR flower4 is null) ");

    // mysqli_stmt_bind_param($statment, "s", $potname);
      // mysqli_stmt_execute($statment);

      $response = array();
      $response["success"] = true;

      echo json_encode($response);

    ?>
