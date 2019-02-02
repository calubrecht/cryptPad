package us.calubrecht.cryptPad;

public class DocChangeEvent
{
  private String event_;
  
  public DocChangeEvent(String event)
  {
    event_ = event;
  }
  
  public String getEvent()
  {
    return event_;
  }
}
