package com.cnksi.sjjc.View;
/**
* @author  Wastrel
* @date 创建时间：2016年8月19日 上午9:12:01
* TODO
*/

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.TextView;

public class AutoFitTextView extends TextView {
	private static float DEFAULT_MIN_TEXT_SIZE = 10;
	private static float DEFAULT_MAX_TEXT_SIZE = 20;
	// Attributes
	private Paint testPaint;
	private float minTextSize;
	private float maxTextSize;
	public AutoFitTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialise();
	}

	private void initialise() {
		testPaint = new Paint();
		testPaint.set(this.getPaint());
		// max size defaults to the intially specified text size unless it is
		// too small
		maxTextSize = this.getTextSize();
		if (maxTextSize <= DEFAULT_MIN_TEXT_SIZE) {
			maxTextSize = DEFAULT_MAX_TEXT_SIZE;
		}
		minTextSize = DEFAULT_MIN_TEXT_SIZE;
	}

	/**
	 * Re size the font so the specified text fits in the text box * assuming the text box is the specified width.
	 */
	private void refitText(String text, int textWidth, int textHeight) {
		if (textWidth > 0) {
			int availableWidth = textWidth - this.getPaddingLeft() - this.getPaddingRight();
			int availableHeight = textHeight - this.getPaddingBottom() - this.getPaddingTop() - 30;
			int autoWidth = availableWidth-(int) (2*maxTextSize);
			float trySize = maxTextSize;
			testPaint.setTextSize(trySize);
			Rect bounds = new Rect();
			while ((trySize > minTextSize)) {
				testPaint.getTextBounds(text, 0, text.length(), bounds);
				int width = bounds.width();
				int height = bounds.height();
				if (width <= autoWidth) {
					break;
				}
				int line = availableHeight / height;
				if (line >= 2) {
					this.setSingleLine(false);
					this.setMaxLines(line);
					autoWidth = availableWidth * line-line*(int)trySize/2;
				}
				trySize -= 1;
				if (trySize <= minTextSize) {
					trySize = minTextSize;
					break;
				}
				testPaint.setTextSize(trySize);
			}
			this.setTextSize(TypedValue.COMPLEX_UNIT_PX, trySize);
		}
	}

	@Override
	protected void onTextChanged(CharSequence text, int start, int before, int after) {
		super.onTextChanged(text, start, before, after);
		refitText(text.toString(), this.getWidth(), this.getHeight());
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		if (w != oldw || h != oldh) {
			refitText(this.getText().toString(), w, h);
		}
	}


}