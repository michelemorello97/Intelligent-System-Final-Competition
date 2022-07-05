import java.util.*;

import javax.swing.text.Position;

import java.awt.Point;
import java.io.*;
import java.math.*;

class Cell{
    int x;
    int y;

    public Cell(int x, int y){
        this.x=x;
        this.y=y;
    } 
}

class Pellet{

    int x;
    int y;
    int value;

    public Pellet() {
    }

    public Pellet(int x, int y, int value){
        this.x=x;
        this.y=y;
        this.value=value;
    }
}

class Map{
    char[][] mappa;
    //int[][] mappa;
    int width;
    int height;
    ArrayList<Pellet> visible;

    public Map(int w, int h){
        visible=new ArrayList<Pellet>();
        this.width=w;
        this.height=h;
        mappa=new char[h][w];
        //mappa=new int[h][w]
    }

    void setVisible(ArrayList<Pellet> visiblePellet){
        this.visible=visiblePellet;
        
        //if char mappa
        for(Pellet p: this.visible){
            if(p.value==10)
                mappa[p.y][p.x]='O';
            else
            mappa[p.y][p.x]='o';
        }

        /*if int mappa
        for(Pellet p: this.visible){
            mappa[p.y][p.x]=p.value;
        }
        */

    }

    void addVisible(Pellet p){

        visible.add(p);
        
        if(p.value==10)
            mappa[p.y][p.x]='O';
        else
            mappa[p.y][p.x]='o';

    }

    void setRow(String row, int i){
        //if char mappa
        for(int j=0; j<width; j++)
            mappa[i][j]=row.charAt(j);
        
        /* if int mappa
        for(int j=0; j<width; j++){
            if(row.charAt(j)=='#')
                mappa[i][j]=11;
            else
                mappa[i][j]=-1;

        }
        */
    }

    void stampa(){
        for(int i=0; i<height; i++)
        System.err.println(mappa[i]);
    }

    boolean checkPellet(int x, int y){
        for(Pellet p: visible)
            if(p.x==x && p.y==y)
                return true;
        return false;
    }
}

class Pacman{

    int pacId;
    int x; 
    int y;
    String typeId;
    int speedTurnsLeft;
    int abilityCooldown;
    ArrayList<Direction> possiblesMoves;
    Direction choice;
    Cell explore;
    String action; //agiungere una variabile intera pe ril controllo in gen output e non usare la stringa stessa.
    String switchTo;
    boolean iChoose;

    public Pacman(int pacId, int x, int y, String typeId, int speedTurnsLeft, int abilityCooldown) {
        this.pacId = pacId;
        this.x = x;
        this.y = y;
        this.typeId = typeId;
        this.speedTurnsLeft = speedTurnsLeft;
        this.abilityCooldown = abilityCooldown;
        possiblesMoves=new ArrayList<Direction>();
        choice=null;
        explore=null;
        action="";
        switchTo="";
        iChoose=false;

    }

    void bestDirection(){
        double max=0.0f;
        for(Direction d: possiblesMoves){
            System.err.println(" verso " + d.direction+" il rapporto è"+d.pointsOnTime);
            if(d.pointsOnTime>max){
                max=d.pointsOnTime;
                this.choice=d;
                this.action="MOVE";
                this.iChoose=true;
            }
        }
        if(this.choice==null)
            System.err.println(" e non ho scelto nulla ");
        else
            System.err.println(" e vado verso " + this.choice.direction);
    }
    
    void tryToWin(Pacman oppo) {
    	if(oppo.typeId.equals("SCISSORS")){
            this.action="SWITCH";
            this.switchTo="ROCK";
        }    	
    		
    	else if(oppo.typeId.equals("ROCK")){
            this.action="SWITCH";
            this.switchTo="PAPER";
        }
    		
    	else if(oppo.typeId.equals("PAPER")){
            this.action="SWITCH";
            this.switchTo="SCISSORS";
        }
    		
    }
}

class Direction{
    String direction;
    double pointsOnTime;

    public Direction(String d, int points, int time){
        direction=d;

        pointsOnTime=(double)points/time;
    }

    void setDirection(String d){
        direction=d;
    }

    void setPointsOnTime(int points, int time){
        pointsOnTime=(Double)pointsOnTime/time;
    }

    void setDirection(Pacman my, Pacman op){
        if(my.y==op.y){
            if(my.x-op.x>=1)
                direction="left";
            else
                direction="right";
        }
        else if(my.x==op.y){
            if(my.y-op.y>=1)
                direction="up";
            else
                direction="down";
        }
    }
}

class GameManager{
    Map board;
    ArrayList<Pacman> myPacmans;
    ArrayList<Pacman> opponents;
    
    ArrayList<Pacman> myLastPosition;
    ArrayList<Pacman> opponentsLastPosition;
    
    int myScore;
    int opponentScore;
    String output;

    public GameManager(int w, int h){
        board=new Map(w, h);
        myPacmans=new ArrayList<Pacman>();
        opponents=new ArrayList<Pacman>();
        output="";
    }

    void stampaMappa(){
        board.stampa();
    }

    void setBoard(String line, int row){
        board.setRow(line, row);
    }

    void addMyPacman(Pacman p){
        myPacmans.add(p);
    }

    void addOppoPacman(Pacman p){
        opponents.add(p);
    }

    void setScores(int my, int oppo){
        myScore=my;
        opponentScore=oppo;
    }

    void setVisible(ArrayList<Pellet> visible){
        board.setVisible(visible);
    }

    void addvisible(Pellet p){
        board.addVisible(p);
    }

    void clearAll(){
        this.output="";
        myLastPosition=myPacmans;
        opponentsLastPosition=opponents;
        myPacmans=new ArrayList<Pacman>();
        opponents=new ArrayList<Pacman>();
        board.visible=new ArrayList<Pellet>();
    }

    void updateMap(){

        for(Pacman p: myPacmans){
            System.err.println("Sono p"+p.pacId+" e sono in update map");
            board.mappa[p.y][p.x]='X';
            boolean up=true, down=true, right=true, left=true;
            int pointsUp=0, pointsDown=0, pointsRight=0, pointsLeft=0;
            int i=1;
            while(up || down || right || left){
                
                if(up){
                    int y=Math.floorMod(p.y - i, board.height);
                    //System.err.println(" la mia Y e "+ p.y + " in MOD è " + y);
                    if(board.mappa[y][p.x]=='#'){
                        up=false;
                        p.possiblesMoves.add(new Direction("up", pointsUp, i));
                        System.err.println(" e vedo sopra di me " + pointsUp + " punti in " + i + " turni");
                    }

                    else if((board.mappa[y][p.x]=='o' || board.mappa[y][p.x]=='O') && board.checkPellet(p.x, y)==false){
                        board.mappa[y][p.x]='X';
                    }

                    else if(board.mappa[y][p.x]=='o')
                        pointsUp++;
                    
                    else if(board.mappa[y][p.x]=='O')
                        pointsUp+=10;

                    
                }
    
                if(down){
                    int y=Math.floorMod(p.y + i, board.height);
                    //System.err.println(" la mia Y e " +p.y + " in MOD è " + y);
                    if(board.mappa[y][p.x]=='#'){
                        down=false;
                        p.possiblesMoves.add(new Direction("down", pointsDown, i));
                        System.err.println(" e vedo sotto di me " + pointsDown + " punti in " + i + " turni");
                    }

                    else if((board.mappa[y][p.x]=='o' || board.mappa[y][p.x]=='O') && board.checkPellet(p.x, y)==false){
                        board.mappa[y][p.x]='X';
                    }

                    else if(board.mappa[y][p.x]=='o')
                        pointsDown++;
                    
                    else if(board.mappa[y][p.x]=='O')
                        pointsDown+=10;

                }
    
                if(right){
                    int x=Math.floorMod(p.x + i, board.width);
                    //System.err.println(" la mia X e " +p.x + " in MOD è " + x);
                    if(board.mappa[p.y][x]=='#' || x==p.x){
                        right=false;
                        p.possiblesMoves.add(new Direction("right", pointsRight, i));
                        System.err.println(" e vedo a destra " + pointsRight + " punti in " + i + " turni");
                    }

                    else if((board.mappa[p.y][x]=='o' || board.mappa[p.y][x]=='O') && board.checkPellet(x, p.y)==false){
                        board.mappa[p.y][x]='X';
                    }    

                    else if(board.mappa[p.y][x]=='o')
                        pointsRight++;
                    
                    else if(board.mappa[p.y][x]=='O')
                        pointsRight+=10;

                }
    
                if(left){
                    int x=Math.floorMod(p.x - i, board.width);
                   // System.err.println(" la mia X e " +p.x + " in MOD è " + x);
                    if(board.mappa[p.y][x]=='#' || x==p.x){
                        left=false;
                        p.possiblesMoves.add(new Direction("left", pointsLeft, i));
                        System.err.println(" e vedo a sinistra " + pointsLeft + " punti in " + i + " turni");
                    }

                    else if((board.mappa[p.y][x]=='o' || board.mappa[p.y][x]=='O') && board.checkPellet(x, p.y)==false){
                        board.mappa[p.y][x]='X';
                    }

                    else if(board.mappa[p.y][x]=='o')
                        pointsLeft++;
                    
                    else if(board.mappa[p.y][x]=='O')
                        pointsLeft+=10;
                }
                
                i++;
            }
        }
    }

    void chooseDirection(){
        for(Pacman p: myPacmans){
             System.err.println("Sono p"+p.pacId+" e sono in choose direction");
            p.bestDirection();
        }
    }

    void checkForNull(){
        for(Pacman p: myPacmans){
             System.err.println("Sono p"+p.pacId+" e sono in check for null e la mia bilita ha " + p.abilityCooldown);
            if(p.iChoose==false){
                if(p.abilityCooldown==0)
            	    p.action="SPEED";
                else
                    explore(p);
            }
        }
    }
    

    void explore(Pacman p){
         System.err.println("Sono p"+p.pacId+" e sono in explore");/*
        ArrayList<Cell> cell = new ArrayList<Cell>();
        ArrayList<Cell> visited = new ArrayList<Cell>();
        cell.add(new Cell(p.x, p.y));
        boolean found=false;
        while(found==false){
            ArrayList<Cell> temp = new ArrayList<Cell>();

            for(Cell c: cell){
                if(board.mappa[Math.floorMod(c.y-1, board.height)][c.x]!='#')
                    temp.add(new Cell(c.x, Math.floorMod(c.y-1, board.height)));
                if(board.mappa[Math.floorMod(c.y+1, board.height)][c.x]!='#')
                    temp.add(new Cell(c.x, Math.floorMod(c.y+1, board.height)));
                if(board.mappa[c.y][Math.floorMod(c.x+1, board.width)]!='#')
                    temp.add(new Cell(Math.floorMod(c.x+1, board.width), c.y));
                if(board.mappa[c.y][Math.floorMod(c.x-1, board.width)]!='#')
                    temp.add(new Cell(Math.floorMod(c.x-1, board.width), c.y));

            }
            visited.addAll(cell);
            cell=new ArrayList<Cell>();

            for(Cell po: temp){
                /*if(presente(visited, po))
                    temp.remove(po);
                else if(board.mappa[po.y][po.x]=='o' || board.mappa[po.y][po.x]=='O' || board.mappa[po.y][po.x]==' '){
                    p.explore=new Cell(po.x, po.y);
                    found=true;
                }*//* 
                if(board.mappa[po.y][po.x]=='o' || board.mappa[po.y][po.x]=='O' || board.mappa[po.y][po.x]==' '){
                    p.explore=new Cell(po.x, po.y);
                    found=true;
                }
                else if(presente(visited, po)==false)
                    cell.add(po);

            }
            //cell=temp;
        }
        p.action="MOVE"; */

        boolean found=false;
        for(int i=1; i<board.width && found==false; i++){
            if(p.y-i>=0 && (board.mappa[p.y-i][p.x]=='o' || board.mappa[p.y-i][p.x]=='O' || board.mappa[p.y-i][p.x]==' ')){
                p.explore=new Cell(p.x, p.y-i);
                found=true;
            }
            else if(p.y+i<board.height && (board.mappa[p.y+i][p.x]=='o' || board.mappa[p.y+i][p.x]=='O' || board.mappa[p.y+i][p.x]==' ')){
                p.explore=new Cell(p.x, p.y+i);
                found=true;
            }
            else if(p.x-i>=0 && (board.mappa[p.y][p.x-i]=='o' || board.mappa[p.y][p.x-i]=='O' || board.mappa[p.y][p.x-i]==' ')){
                p.explore=new Cell(p.x-i, p.y);
                found=true;
            }
            else if(p.x+i<board.width && (board.mappa[p.y][p.x+i]=='o' || board.mappa[p.y][p.x+i]=='O' || board.mappa[p.y][p.x+i]==' ')){
                p.explore=new Cell(p.x+i, p.y);
                found=true;
            }
            else if(p.y-i>=0 && p.x-i>=0 && (board.mappa[p.y-i][p.x-i]=='o' || board.mappa[p.y-i][p.x-i]=='O' || board.mappa[p.y-i][p.x-i]==' ')){
                p.explore=new Cell(p.x-i, p.y-i);
                found=true;
            }
            else if(p.y+i<board.height && p.x+i<board.width && (board.mappa[p.y+i][p.x+i]=='o' || board.mappa[p.y+i][p.x+i]=='O' || board.mappa[p.y+i][p.x+i]==' ')){
                p.explore=new Cell(p.x+i, p.y+i);
                found=true;
            }
            else if(p.y-i>=0 && p.x+i<board.width && (board.mappa[p.y-i][p.x+i]=='o' || board.mappa[p.y-i][p.x+i]=='O' || board.mappa[p.y-i][p.x+i]==' ')){
                p.explore=new Cell(p.x+i, p.y-i);
                found=true;
            }
            else if(p.y+i<board.height && p.x-i>=0 && (board.mappa[p.y+i][p.x-i]=='o' || board.mappa[p.y+i][p.x-i]=='O' || board.mappa[p.y+i][p.x-i]==' ')){
                p.explore=new Cell(p.x-i, p.y+i);
                found=true;
            }
            
        }
        p.action="MOVE";

    }

    boolean presente(ArrayList<Cell> cell, Cell c){
        for(Cell k: cell){
            if(k.x==c.x && k.y==c.y)
                return true;
        }
        return false;
    }
    
    void checkForFight() {
    	for(Pacman p: myPacmans) {
             System.err.println("Sono p"+p.pacId+" e sono in check for fight");
    		Pacman near=isOpponentNear(p);
    		if(near!=null) {
    			if(p.abilityCooldown==0 && near.abilityCooldown!=0) {
    				if(fightResult(p, near)==1){ //vittoria
    					p.action="MOVE";
                        if(p!=null && near!=null)
                            p.choice.setDirection(p, near);
                    }
    				else // sconfita o pareggio
    					p.tryToWin(near);
    			}
    			else if(p.abilityCooldown==0 && near.abilityCooldown==0) {
    				if(fightResult(p, near)==1){ //vittoria
    					p.action="WAIT";
                    }
    				else //sconfita o pareggio
    					p.action="SPEED";
    			}
    			else if(p.abilityCooldown!=0 && near.abilityCooldown!=0) {
    				if(fightResult(p, near)==1){ //vittoria
    					p.action="MOVE";
                        p.choice.setDirection(p, near);
                    }
    				
    			}
    		}
    	}
    }
    
    int fightResult(Pacman my, Pacman oppo) {
    	if(my.typeId.equals(oppo.typeId))
    		return 0;
    	else if((my.typeId.equals("ROCK") && oppo.typeId.equals("SCISSORS")) || (my.typeId.equals("PAPER") && oppo.typeId.equals("ROCK")) || (my.typeId.equals("SCISSORS") && oppo.typeId.equals("PAPER")))
    		return 1;
    	else 
    		return -1;
    }
    
    Pacman isOpponentNear(Pacman p) {
    	Pacman op=null;
    	for(Pacman oppo: opponents) {
    		if((p.y==oppo.y && (p.x+1 == oppo.x || p.x-1 == oppo.x)))
    			op=oppo;
    		else if((p.x==oppo.x && (p.y+1 == oppo.y || p.y-1 == oppo.y)))
    			op=oppo;
    	}
    	
    	return op;
    }




    void genOutput(){
        String temp="";
        for(Pacman p: myPacmans){
            if(p.typeId.equals("DEAD")==false){
            	
            	if(p.action.equals("MOVE")) {
            		temp+="| " + p.action+ " " + Integer.toString(p.pacId) + " ";

                    if(p.choice==null)
                        temp+=Integer.toString(p.explore.x) + " " + Integer.toString(p.explore.y) + " ";

                    else if(p.choice.direction.equals("up"))
                        temp+=Integer.toString(p.x) + " " + Integer.toString(Math.floorMod(p.y-1, board.height)) + " ";
                    
                    else if(p.choice.direction.equals("down"))
                        temp+=Integer.toString(p.x) + " " + Integer.toString(Math.floorMod(p.y+1, board.height)) + " ";
                        
                    else if(p.choice.direction.equals("right"))
                        temp+=Integer.toString(Math.floorMod(p.x+1, board.width)) + " " + Integer.toString((p.y)) + " ";
                    
                    else if(p.choice.direction.equals("left"))
                        temp+=Integer.toString(Math.floorMod(p.x-1, board.width)) + " " + Integer.toString((p.y)) + " ";
            	}
                else if(p.action.equals("WAIT")) {
            		temp+="| MOVE " + Integer.toString(p.pacId) + " ";
            		temp+=Integer.toString(p.x) + " " + Integer.toString(p.y) + " ";
            	} 
                else if(p.action.equals("SPEED")) {
            		temp+="| " + p.action + " " + Integer.toString(p.pacId)+ " ";
            	}
            	else if(p.action.equals("SWITCH")) {
            		temp+="| " + p.action + " " + Integer.toString(p.pacId)+ " " + p.switchTo + " ";
            	}
            	
            	              
            }
        }
        
        output=temp;//.substring(2);
    }
    

    void play(){
        //osserva e scegli
        updateMap();
        chooseDirection();

        //check pacman directions
        checkForNull();
        checkForFight(); // TODO se vediamo un nemico nel corridoio entro 4/3 caselle, controllare se ha il cooldown dell'abilita' !=0 
        				 //e fare la differenza fra le posizioni passate e correnti, perche' potrebbe mangiarci senza darci il tempo di trasformarci

        //move
        genOutput();
    }
}

class Player {

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);

        int width = in.nextInt(); // size of the grid
        int height = in.nextInt(); // top left corner is (x=0, y=0)

        GameManager gm=new GameManager(width, height);

        if (in.hasNextLine()) {
            in.nextLine();
        }
        for (int i = 0; i < height; i++) {
            String row = in.nextLine(); // one line of the grid: space " " is floor, pound "#" is wall
            gm.setBoard(row, i);
        }
        gm.stampaMappa();

        // game loop
        while (true) {
            int myScore = in.nextInt();
            int opponentScore = in.nextInt();
            int visiblePacCount = in.nextInt(); // all your pacs and enemy pacs in sight

            gm.setScores(myScore, opponentScore);


            for (int i = 0; i < visiblePacCount; i++) {
                int pacId = in.nextInt(); // pac number (unique within a team)
                boolean mine = in.nextInt() != 0; // true if this pac is yours
                int x = in.nextInt(); // position in the grid
                int y = in.nextInt(); // position in the grid
                String typeId = in.next(); // unused in wood leagues
                int speedTurnsLeft = in.nextInt(); // unused in wood leagues
                int abilityCooldown = in.nextInt(); // unused in wood leagues

                Pacman p=new Pacman(pacId, x, y, typeId, speedTurnsLeft, abilityCooldown);
                
                if(mine)
                    gm.addMyPacman(p);
                else
                    gm.addOppoPacman(p);
            }


            int visiblePelletCount = in.nextInt(); // all pellets in sight
            for (int i = 0; i < visiblePelletCount; i++) {
                int x = in.nextInt();
                int y = in.nextInt();
                int value = in.nextInt(); // amount of points this pellet is worth

                Pellet p=new Pellet(x, y, value);
                gm.addvisible(p);

            }
            gm.play();
            //gm.stampaMappa();
            // Write an action using System.out.println()
            // To debug: System.err.println("Debug messages...");

            System.out.println(gm.output); // MOVE <pacId> <x> <y>
            gm.clearAll();
        }
    }
}
