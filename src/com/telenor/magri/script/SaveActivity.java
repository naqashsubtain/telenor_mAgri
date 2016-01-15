/*  1:   */ package com.telenor.magri.script;
/*  2:   */ 
/*  3:   */ import com.agiserver.helper.DBHelper;
/*  4:   */ import com.agiserver.helper.DatabaseException;
/*  5:   */ import com.agiserver.helper.common.AbstractBaseAgiScript;
/*  6:   */ import org.apache.log4j.Logger;
/*  7:   */ import org.asteriskjava.fastagi.AgiChannel;
/*  8:   */ import org.asteriskjava.fastagi.AgiException;
/*  9:   */ import org.asteriskjava.fastagi.AgiRequest;
/* 10:   */ 
/* 11:   */ public class SaveActivity
/* 12:   */   extends AbstractBaseAgiScript
/* 13:   */ {
/* 14:22 */   private static final Logger logger = Logger.getLogger(SaveActivity.class);
/* 15:   */   
/* 16:   */   public SaveActivity()
/* 17:   */     throws DatabaseException
/* 18:   */   {
/* 19:26 */     super(true);
/* 20:   */   }
/* 21:   */   
/* 22:   */   public void service(AgiRequest request, AgiChannel channel)
/* 23:   */     throws AgiException
/* 24:   */   {
/* 25:   */     try
/* 26:   */     {
/* 27:35 */       Integer start_time = Integer.valueOf(channel
/* 28:36 */         .getVariable("START_TIME"));
/* 29:37 */       Integer end_time = Integer.valueOf(channel
/* 30:38 */         .getVariable("END_TIME"));
/* 31:39 */       Integer diff = Integer.valueOf(channel.getVariable("DIFF"));
/* 32:40 */       String activityType = channel.getVariable("ACTIVITY_TYPE");
				String context_name = channel.getVariable("CONTEXT_NAME");

					
						if(activityType=="null")
						{
							activityType="CRICKET_WELCOME";
						}
					
/* 33:41 */       String dtmf = channel.getVariable("DTMF");
/* 34:42 */       String cellno = "0" + request.getParameter("CALLER_NUM");
/* 35:   */       
/* 36:44 */       logger.debug("Start_Time = " + start_time);
/* 37:45 */       logger.debug("End_Time = " + end_time);
/* 38:46 */       logger.debug("Diff = " + diff);
/* 39:47 */       logger.debug("Activity_Type = " + activityType);
					logger.debug("context name = " + context_name);
/* 40:   */       
/* 41:49 */       StringBuilder dml = new StringBuilder(300);
/* 42:50 */       dml
/* 43:51 */         .append("insert into activity(cellno,dt,activity_type,duration,dtmf,context)");
/* 44:52 */       dml.append(" values(?, now(), ?, ?,?,?)");
/* 45:53 */       DBHelper.getInstance().executeDml(dml.toString(), 
/* 46:54 */         super.getConnection(), 
/* 47:55 */         new Object[] { cellno, activityType, diff, dtmf, context_name });
/* 48:56 */       channel.setVariable("__SAVE_ACTIVITY", "YES");
/* 49:57 */       logger.info("Save successfully -  activity typye = " + 
/* 50:58 */         activityType + ": diff = " + diff + " , cellno = " + 
/* 51:59 */         cellno + "context name: " + context_name );
/* 52:60 */       return;
/* 53:   */     }
/* 54:   */     catch (DatabaseException databaseException)
/* 55:   */     {
/* 56:64 */       logger.error(databaseException.getMessage(), 
/* 57:65 */         databaseException);
/* 58:   */     }
/* 59:   */     catch (Exception ex)
/* 60:   */     {
/* 61:69 */       logger.error(ex.getMessage(), ex);
/* 62:   */     }
/* 63:72 */     channel.setVariable("__SAVE_ACTIVITY", "NO");
/* 64:   */   }
/* 65:   */ }



/* Location:           C:\Users\mateen\Desktop\Novels\cricket_agi.jar

 * Qualified Name:     com.cricket.script.SaveActivity

 * JD-Core Version:    0.7.0.1

 */