
public class ComplexEvent extends Event{

	private Event[] eventsInvolvedInComplexEvent;
	public ComplexEvent(GenomicCoordinate c1, GenomicCoordinate c2,
			EVENT_TYPE type, Event[] involvedEvents, GenomicNode hostingNode) {
		super(c1, c2, type);
		this.eventsInvolvedInComplexEvent = involvedEvents;
		super.setNode(hostingNode, true);
		super.setNode(hostingNode, false);
	}

	public Event[] getEventsInvolvedInComplexEvent(){
		return this.eventsInvolvedInComplexEvent;
	}

}
