<?php
  $con = mysqli_connect("localhost", "root", "smartpot", "arduino", "3307");
  $potCode = $_POST["id"];

  $query = "SELECT auto, flower FROM smartpot where potCode = '$potCode' ";
  $result = mysqli_query($con, $query);

  // $query2 = "SELECT flower FROM smartpot where  = '$potCode' ";
  // $result2 = mysqli_query($con, $query2);
  // $row3 = mysqli_fetch_array($result2)
  // // 사용자의 꽃의 정보를 받아옴
  // $query3 = "SELECT idx FROM flower where idx = $row3[0] ";
  // $result3 = mysqli_query($con, $query3);

  while ($row = mysqli_fetch_array($result)) {
    echo '?'. $row[0]. $row[1];
  }

  // while ($row2 = mysqli_fetch_array($result3)) {
  //   echo '?'. $row2[0];
  // }

  mysqli_close($con);
?>
