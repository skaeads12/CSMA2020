# CSMA2020 개요

* CSMA/CD 구현

# Classes

* Main: Link, Node 클래스 객체 생성, maxCycle과 Node 객체 갯수, 각 Node의 Data Transmission 발생 분기를 관리하기 위한 확률등 Parameter 세팅

* Link: Common Carrier 역할을 할 객체, isUsing 메소드로 현재 Node에 의해 Common Carrier가 사용되는 지 판단

* Node: 랜덤으로 Data Transmission 이벤트가 발생하며, 도착지도 랜덤, isSending, isReceiving, isWaiting등 메소드로 데이터 전송이 가능한 지 판단

# Written by

* 정민수, 신종화, 홍성태
