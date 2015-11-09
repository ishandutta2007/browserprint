function fixDates(selectorID){
	$('#' + selectorID + ' > option').each(function() {
		var regex = /^(\d+)\/(\d+)\/(\d+), (\d+):(\d+):(\d+) UTC$/;
		var match = regex.exec($(this).text());
		if(match != null){
			var newDate = new Date(Date.UTC(match[3], match[2], match[1], match[4], match[5], match[6], 0));
			$(this).text($.format.date(newDate, 'dd/MM/yyyy, HH:mm:ss'));
		}
	});
}

function onClickHistory1(){
	$("#historySelector1").prop('disabled', false);
	$("#UuidTextbox1").prop('disabled', true);
}

function onClickUUID1(){
	$("#UuidTextbox1").prop('disabled', false);
	$("#historySelector1").prop('disabled', true);
}

function onClickHistory2(){
	$("#historySelector2").prop('disabled', false);
	$("#UuidTextbox2").prop('disabled', true);
}

function onClickUUID2(){
	$("#UuidTextbox2").prop('disabled', false);
	$("#historySelector2").prop('disabled', true);
}