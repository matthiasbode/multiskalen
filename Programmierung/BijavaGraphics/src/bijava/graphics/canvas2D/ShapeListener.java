package bijava.graphics.canvas2D;

public interface ShapeListener {
	public void entered(ShapeEvent e);
	public void exited(ShapeEvent e);
	public void clicked(ShapeEvent e);
}
