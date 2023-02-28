from kivy.app import App
from kivy.lang import Builder
from kivy import platform
from kivy.uix.screenmanager import ScreenManager, Screen, NoTransition, SwapTransition

App_Layout = ("""
#:import ZBarCam kivy_garden.zbarcam.ZBarCam
#:import ZBarSymbol pyzbar.pyzbar.ZBarSymbol
BoxLayout:
    orientation: 'vertical'
    ZBarCam:
        id: zbarcam
    Label:
        id: qrtext
        text: str('--- ! NO DETECTION !  ---') if [str(symbol.data)[2:-1] for symbol in zbarcam.symbols] == [] else str([(symbol.data) for symbol in zbarcam.symbols])[3:-2]
        color: (1,0,0,1) if [str(symbol.data)[2:-1] for symbol in zbarcam.symbols] == [] else (0,1,0,1)
        font_size: '60px'
        size_hint: (1, .2)
    Button:
        text: 'Exit'
        background_color: (1,0,0,1)
        font_size: '60px'
        size_hint: (1, .2)
        on_press: app.stop()
""")

class DemoApp(App):
    def build(self):
        self.sm = ScreenManager(transition=SwapTransition())
        self.screen = Screen()
        self.layout = Builder.load_string(App_Layout)  
        self.screen.add_widget(self.layout)
        self.sm.add_widget(self.screen)

        if platform == 'android':
            from android.permissions import request_permissions, check_permission, Permission
            request_permissions([Permission.CAMERA])     
        
        return self.sm

    def on_leave(self):
        self.layout.ids.zbarcam.ids['xcamera']._camera._device.release()

if __name__ == '__main__':
    DemoApp().run()