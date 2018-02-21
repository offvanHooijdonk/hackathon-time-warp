package by.epam.hackathon.timewarp.ui.tasklist


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import by.epam.hackathon.timewarp.R
import by.epam.hackathon.timewarp.model.TaskModel
import kotlinx.android.synthetic.main.fr_tasks.*
import kotlinx.android.synthetic.main.item_task_imported.view.*

class TasksFragment : Fragment() {

    companion object {
        fun getInstance(): TasksFragment {
            return TasksFragment()
        }
    }

    lateinit var adapter: TasksAdapter
    lateinit var tasksList: MutableList<TaskModel>

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater!!.inflate(R.layout.fr_tasks, container, false)


    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tasksList = ArrayList()
        initTempList()

        adapter = TasksAdapter(tasksList)
        rvTasks.adapter = adapter
        rvTasks.layoutManager = LinearLayoutManager(activity)
    }

    private fun initTempList() {
        tasksList.add(TaskModel(1L, "1", "Test task", TaskModel.Source.JIRA))
        tasksList.add(TaskModel(3L, "2364", "New task", TaskModel.Source.TIME))
        tasksList.add(TaskModel(5L, "5h1", "More tasks", TaskModel.Source.TIME))
        tasksList.add(TaskModel(6L, "5tk", "Another one task", TaskModel.Source.JIRA))
        tasksList.add(TaskModel(8L, "str4", "A task with a longer name so you could see ellipsize", TaskModel.Source.TASK))
    }

    class TasksAdapter(val models: List<TaskModel>) : RecyclerView.Adapter<TasksAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder =
                ViewHolder(LayoutInflater.from(parent!!.context).inflate(R.layout.item_task_imported, parent, false))


        override fun getItemCount(): Int = models.size

        override fun onBindViewHolder(vh: ViewHolder?, position: Int) {
            vh!!.bindData(models[position])
        }

        class ViewHolder(v: View?) : RecyclerView.ViewHolder(v) {
            fun bindData(model: TaskModel) {
                itemView.txtTaskTitle.text = model.title
                itemView.imgTaskTypeJira.visibility = if (model.source == TaskModel.Source.JIRA) View.VISIBLE else View.INVISIBLE
                itemView.imgTaskTypeTime.visibility = if (model.source == TaskModel.Source.TIME) View.VISIBLE else View.INVISIBLE
                itemView.imgTaskTypeTask.visibility = if (model.source == TaskModel.Source.TASK) View.VISIBLE else View.INVISIBLE
            }
        }

    }
}