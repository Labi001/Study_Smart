package com.labinot.bajrami.study_smart.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.labinot.bajrami.study_smart.R
import com.labinot.bajrami.study_smart.presentation.domain.model.Task
import com.labinot.bajrami.study_smart.presentation.domain.TaskCheckBox
import com.labinot.bajrami.study_smart.presentation.domain.utils.Priority
import com.labinot.bajrami.study_smart.presentation.domain.utils.changeMillisToDateString


fun LazyListScope.taskList(
    sectionTitle:String,
    emptyListText:String,
    tasks: List<Task>,
    onTaskCardClick: (Int?) -> Unit,
    onCheckBoxClick: (Task) -> Unit
){

    item {

        Text(text = sectionTitle,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Start,
            modifier = Modifier.padding(12.dp)
        )

        if(tasks.isEmpty()){

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Image(modifier = Modifier
                    .size(120.dp),
                    painter = painterResource(id = R.drawable.check),
                    contentDescription = emptyListText
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(modifier = Modifier.fillMaxWidth(),
                    text = emptyListText,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    textAlign = TextAlign.Center)



            }

        }


    }

    items(tasks){ task->

        TaskCard(task = task,
            onCheckBoxClick = { onCheckBoxClick(task) },
            onClick = {onTaskCardClick(task.taskId)})

    }

    
}

@Composable
private fun TaskCard(
    modifier: Modifier = Modifier,
    task: Task,
    onCheckBoxClick: () -> Unit,
    onClick: () -> Unit

){

    ElevatedCard(
        modifier = modifier.clickable { onClick.invoke() }
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {

        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically) {

            TaskCheckBox(isCompleted = task.isCompleted,
                borderColor = Priority.fromInt(task.priority).color,
                onCheckBoxClicked = onCheckBoxClick)

             Spacer(modifier = Modifier.width(10.dp))

            Column {

                Text(text = task.tittle,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleMedium,
                    textDecoration = if (task.isCompleted){
                        TextDecoration.LineThrough
                    }else TextDecoration.None)

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = task.dueDate.changeMillisToDateString(),
                    style = MaterialTheme.typography.bodySmall)

            }

        }

    }



}