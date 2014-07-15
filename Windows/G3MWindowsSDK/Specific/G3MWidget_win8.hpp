


#ifndef __G3MWindowsSDK_G3MWidget_win8__
#define __G3MWindowsSDK_G3MWidget_win8__

class G3MWidget;
class D3DRenderer;


#include "GL.hpp"

class G3MWidget_win8{
private:
	G3MWidget* _genericWidget;
	D3DRenderer* _renderer;
	

public:
	G3MWidget_win8();
	GL* getGL();
	D3DRenderer* getRenderer();
	void initSingletons() const;
	void setWidget(G3MWidget* genericWidget);
};


#endif