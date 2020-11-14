(function($) {    
    $.anwidget("jl.audio", {
        options: {
			'visible': true,
			'position': 'absolute'
        },
		_props: ["left", "top", "width", "height", "position", "transform-origin", "transform"],
		_attrs: ["id", "src", "autoplay", "class", "controls", "muted", "loop", "preload"],
		getCreateOptions: function() {
			return $.extend(this.options, { 'id': "audio" + _widgetID++ });
		},
		getCreateString: function() {
			return "<audio>";
		},
		getProperties: function() {
			return this._props;
		},
		getAttributes: function() {
			return this._attrs;
		}    
	});   
})(jQuery);