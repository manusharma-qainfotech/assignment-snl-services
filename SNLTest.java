package com.googlerestapi.com.rest;

import static org.hamcrest.Matchers.equalTo;
import static org.testng.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class SNLTest {
	int boardId;
	int playerId;
@BeforeTest
public void newBoard() throws ParseException
{

	Response response = RestAssured.given().param("response.status", "1").when().get("http://10.0.1.86/snl/rest/v1/board/new.json");
      response.then().assertThat().statusCode(200);
		String jsonData = response.asString();
	    JSONParser parser = new JSONParser();
	    JSONObject obj = (JSONObject)parser.parse(jsonData);
	    JSONObject resp=(JSONObject)obj.get("response");
	    JSONObject boards=(JSONObject)resp.get("board");
	    boardId  = Integer.parseInt(boards.get("id").toString()); 

}
@Test(priority =1)
public void testNewBoard()
{    
	Response response = RestAssured.when().get("http://10.0.1.86/snl/rest/v1/board/"+boardId+".json");
	
	response.then().assertThat().statusCode(200);
	response.then().body("response.board.id",equalTo(boardId));
	ArrayList listBoardResponse = RestAssured.when().get("http://10.0.1.86/snl/rest/v1/board.json").jsonPath().get("response.board.id");
	assertEquals(listBoardResponse.contains(boardId),true);
	
	}
@Test(priority=2)
public void testAddNewPlayer()
{
	JSONObject objj = new JSONObject();
	objj.put("board",boardId);
	JSONObject playerObj = new JSONObject();
	playerObj.put("name", "manu");
	objj.put("player",playerObj);
	String json =objj.toJSONString();
	Response response = 	RestAssured.given().contentType(ContentType.JSON).body(json).when().post("http://10.0.1.86/snl/rest/v1/player.json");
	 playerId =  response.jsonPath().getInt("response.player.id");
	 RestAssured.get("http://10.0.1.86/snl/rest/v1/player/"+playerId+".json")
	 .then()
	 .body("response.player.id", equalTo(playerId))
	 .body("response.player.board_id", equalTo(boardId))
	 .body("response.player.name", equalTo("manu"))
	 .body("response.player.position", equalTo(0));
/*
 * check new player from board
 * 
 */
	JsonPath jsonPath = RestAssured.when().get("http://10.0.1.86/snl/rest/v1/board/"+boardId+".json").jsonPath();
    ArrayList listPlayers  = (ArrayList)jsonPath.get("response.board.players");
 	int size  = listPlayers.size();
    int hav=0;
 	for(int i=0;i<size;i++)
    {
   	int id =	(Integer) ((HashMap<String,Object>)listPlayers.get(i)).get("id");
    if(id==playerId)
    	hav = id;
      break;
    }
 	assertEquals(hav,playerId);
	}

@Test(priority=4)
public void testDeleteBoard()
{
	Response response = RestAssured.when().delete("http://10.0.1.86/snl/rest/v1/board/"+boardId+".json");
	response.then().assertThat().statusCode(200);
	response.then().body("response.success", equalTo("OK"));
    RestAssured.when().delete("http://10.0.1.86/snl/rest/v1/board/"+boardId+".json").then().statusCode(500);
    ArrayList listBoardResponse = RestAssured.when().get("http://10.0.1.86/snl/rest/v1/board.json").jsonPath().get("response.board.id");
	assertEquals(listBoardResponse.contains(boardId),false);
    
}
@Test(priority=3)
public void testUpdatePlayerDetails()
{               


}

}
